/*
    Copyright (C) 2013-2026 Nicola L.C. Talbot
    www.dickimaw-books.com

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/
package com.dickimawbooks.texparserlib;

import java.util.Vector;

import java.awt.Color;

import com.dickimawbooks.texparserlib.primitives.Undefined;

/**
 * Represents the (possibly nested) scoping.
 * This is now implemented using a list of {@link TeXSettings}
 * objects instead of have a parent within <code>TeXSettings</code>.
 */
public class Scoping
{
   public Scoping(TeXParser parser)
   {
      if (parser == null)
      {
         throw new NullPointerException();
      }

      this.parser = parser;
      scopingList = new Vector<TeXSettings>();

      globalSettings = new TeXSettings(parser);
   }

   /**
    * Starts a new local scope.
    * This method creates a new {@link TeXSettings} object and adds it
    * to the list.
    * @return the new local scope
    */
   public TeXSettings startGroup()
   {
      TeXSettings settings = new TeXSettings(parser);

      scopingList.add(settings);

      if (parser.isDebugMode(TeXParser.DEBUG_SETTINGS))
      {
         parser.logMessage("START GROUP ID "+settings.getID());
      }

      return settings;
   }

   /**
    * Ends the current scope.
    * This method removes the most recently added {@link TeXSettings} object and
    * returns it.
    * @return the removed scope
    * @throws TeXSyntaxException if there is no local scope to
    * remove
    */
   public TeXSettings endGroup() throws TeXSyntaxException
   {
      if (scopingList.isEmpty())
      {
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_UNEXPECTED_EG);
      }

      TeXSettings settings = scopingList.remove(scopingList.size()-1);

      if (parser.isDebugMode(TeXParser.DEBUG_SETTINGS))
      {
         parser.logMessage("ENDING GROUP ID "+settings.getID());

         parser.logMessage("RETURNING TO GROUP ID "+getCurrentSettings().getID());
      }

      return settings;
   }

   /**
    * Gets the current settings.
    * @return the current settings or the global settings if no
    * local scoping
    */
   public TeXSettings getCurrentSettings()
   {
      return scopingList.isEmpty() ? globalSettings : scopingList.lastElement();
   }

   /**
    * Gets the global (root) settings.
    * @return the global settings
    */
   public TeXSettings getGlobalSettings()
   {
      return globalSettings;
   }

   public void addAfterGroup(TeXObject obj)
   {
      getCurrentSettings().addAfterGroup(obj);
   }

   /**
    * Checks if there is a control sequence or register defined.
    * Defined in this case means not null and not an instance of
    * <code>Undefined</code>.
    * @param csname the control sequence name
    * @return true if there is control sequence or register with the given name that is not an
    * instance of <code>Undefined</code>
    */
   public boolean isControlSequenceDefined(String csname)
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         ControlSequence cs = scopingList.get(i).getControlSequence(csname);

         if (cs instanceof Undefined)
         {
            return false;
         }
         else if (cs != null)
         {
            return true;
         }
      }

      return globalSettings.isDefinedInCurrentScope(csname);
   }

   /**
    * Removes the control sequence.
    * This method removes all control sequences with the given name from all scoping and
    * global settings.
    * If <code>removeEnd</code> is true and the control sequence is a declaration,
    * the end declaration is also removed.
    * @param cs the control sequence
    * @param removeEnd if true, remove the end declaration as well,
    * if applicable
    */
   public void removeControlSequence(ControlSequence cs, boolean removeEnd)
   {
      String csname = cs.getName();
      String endname = null;

      if (removeEnd && cs instanceof Declaration)
      {
         EndDeclaration endDecl = ((Declaration)cs).getEndDeclaration();

         if (endDecl == null)
         {
            endname = "end" + csname;
         }
         else
         {
            endname = endDecl.getName();
         }
      }

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         TeXSettings settings = scopingList.get(i);

         settings.removeControlSequence(csname);

         if (endname != null)
         {
            settings.removeControlSequence(endname);
         }
      }

      globalSettings.removeControlSequence(csname);

      if (endname != null)
      {
         globalSettings.removeControlSequence(endname);
      }
   }

   /**
    * Removes the control sequence.
    * This method removes all control sequences with the given name from all scoping and
    * global settings.
    * @param csname the control sequence name
    * @return the first control sequence with the given name to be
    * removed or null if none removed
    */
   public ControlSequence removeControlSequence(String csname)
   {
      ControlSequence controlSeq = null;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         TeXSettings settings = scopingList.get(i);

         ControlSequence cs = settings.removeControlSequence(csname);

         if (controlSeq == null && cs != null)
         {
            controlSeq = cs;
         }
      }

      ControlSequence cs = globalSettings.removeControlSequence(csname);

      if (controlSeq == null && cs != null)
      {
         controlSeq = cs;
      }

      return controlSeq;
   }

   /**
    * Removes the control sequence from the current scope.
    * If the control sequence is still defined (from an outer scope
    * or globally), this method will add an Undefined control
    * sequence with the same name to the current scope.
    * @param csname the control sequence name
    */
   public void undefControlSequence(boolean isLocal, String csname)
   {
      if (isLocal)
      {
         getCurrentSettings().removeControlSequence(csname);

         if (getControlSequence(csname) != null)
         {
            getCurrentSettings().putControlSequence(
              parser.getListener().createUndefinedCs(csname));
         }
      }
      else
      {
         removeControlSequence(csname);
      }
   }

   /**
    * Add a control sequence. This mimics locally or globally
    * defining (or redefining) a command. If there is already a
    * control sequence with the same name in the current scope it
    * will be overridden.
    *
    * In the local case, if there is a control
    * sequence with the same name in a parent scope then that scope
    * will be unaffected and that control sequence will still be
    * present when that parent scope later becomes the current scope.
    *
    * In the global case, all control sequences with the same name
    * will be removed from the local list and the control sequence
    * will be put in the global (root) settings (replacing any
    * existing control sequence with the same name).
    *
    * @param isLocal if true, put the control sequence in the current
    * settings
    * @param cs the control sequence
    * @param addEnd if the control sequence is a declaration, add
    * the end declaration
    */
   public void putControlSequence(boolean isLocal, ControlSequence cs, boolean addEnd)
   {
      EndDeclaration endDecl = null;

      if (addEnd && cs instanceof Declaration)
      {
         endDecl = ((Declaration)cs).getEndDeclaration();

         if (endDecl == null)
         {
            endDecl = new EndDeclaration(cs.getName());
         }
      }

      if (isLocal)
      {
         if (parser.isDebugMode(TeXParser.DEBUG_CS))
         {
            parser.logMessage("Locally defining "+cs);
         }

         getCurrentSettings().putControlSequence(cs);

         if (endDecl != null)
         {
            if (parser.isDebugMode(TeXParser.DEBUG_CS))
            {
               parser.logMessage("Locally defining "+endDecl);
            }

            getCurrentSettings().putControlSequence(endDecl);
         }
      }
      else
      {
         String csname = cs.getName();

         for (int i = scopingList.size()-1; i >= 0; i--)
         {
            TeXSettings settings = scopingList.get(i);

            settings.removeControlSequence(csname);

            if (endDecl != null)
            {
               settings.removeControlSequence(endDecl.getName());
            }
         }

         if (parser.isDebugMode(TeXParser.DEBUG_CS))
         {
            parser.logMessage("Globally defining "+cs);
         }

         globalSettings.putControlSequence(cs);

         if (endDecl != null)
         {
            if (parser.isDebugMode(TeXParser.DEBUG_CS))
            {
               parser.logMessage("Globally defining "+endDecl);
            }

            globalSettings.putControlSequence(endDecl);
         }
      }
   }

   /**
    * Gets a control sequence by name.
    * This method starts by searching the local scopes and then
    * searches the global scope. The return value may be a control
    * sequence, register or null. If the control sequence was
    * locally undefined the return value may be null or an instance of
    * <code>Undefined</code>. (An <code>Undefined</code> control sequence allows 
    * the previous definition to be restored outside of the scope.)
    * @param csname the control sequence name
    * @return the first control sequence or register found with the
    * given name or null if not found
    */
   public ControlSequence getControlSequence(String csname)
   {
      ControlSequence cs = null;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         cs = scopingList.get(i).getControlSequence(csname);

         if (cs != null)
         {
            if (parser.isDebugMode(TeXParser.DEBUG_CS))
            {
               parser.logMessage("Fetched LOCAL control sequence: "+cs);
            }

            return cs;
         }
      }

      cs = globalSettings.getControlSequence(csname);

      if (parser.isDebugMode(TeXParser.DEBUG_CS))
      {
         if (cs == null)
         {
            parser.logMessage("No control sequence found for: "+csname);
         }
         else
         {
            parser.logMessage("Fetched GLOBAL control sequence: "+cs);
         }
      }

      return cs;
   }

   /**
    * Removes the register.
    * This method removes all registers with the given name from all scoping and
    * global settings.
    * @param csname the register's control sequence name
    * @return the first register with the given name to be
    * removed or null if none removed
    */
   public Register removeRegister(String csname)
   {
      Register register = null;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         TeXSettings settings = scopingList.get(i);

         Register reg = settings.removeRegister(csname);

         if (register == null && reg != null)
         {
            register = reg;
         }
      }

      Register reg = globalSettings.removeRegister(csname);

      if (register == null && reg != null)
      {
         register = reg;
      }

      return register;
   }

   /**
    * Removes the register from the current scope.
    * If the register is still defined (from an outer scope
    * or globally), this method will add an Undefined control
    * sequence with the same name to the current scope.
    * @param csname the register's control sequence name
    */
   public void undefRegister(boolean isLocal, String csname)
   {
      if (isLocal && !scopingList.isEmpty())
      {
         getCurrentSettings().removeRegister(csname);

         if (getControlSequence(csname) != null)
         {
            getCurrentSettings().putControlSequence(
              parser.getListener().createUndefinedCs(csname));
         }
      }
      else
      {
         removeRegister(csname);
      }
   }
   /**
    * Adds a register locally or globally. If local, the register is
    * cloned to allow the content to be locally changed in the
    * current scope.
    * @param isLocal true if local
    * @param reg the register
    * @return the register that was added to the local/global
    * setting (the cloned value in the case of local)
    */
   public Register putRegister(boolean isLocal, Register reg)
   {
      if (isLocal)
      {
         if (parser.isDebugMode(TeXParser.DEBUG_CS))
         {
            parser.logMessage("Locally defining "+reg);
         }

         reg = (Register)reg.clone();

         getCurrentSettings().putRegister(reg);
      }
      else
      {
         String csname = reg.getName();

         for (int i = scopingList.size()-1; i >= 0; i--)
         {
            TeXSettings settings = scopingList.get(i);

            settings.removeControlSequence(csname);
         }

         if (parser.isDebugMode(TeXParser.DEBUG_CS))
         {
            parser.logMessage("Globally defining "+reg);
         }

         globalSettings.putRegister(reg);
      }

      return reg;
   }

   /**
    * Gets a named numeric register.
    * @param name the register's control sequence name
    * @throws TeXSyntaxException if a register is found with the
    * given name but is not numeric
    */
   public NumericRegister getNumericRegister(String name)
    throws TeXSyntaxException
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         NumericRegister reg = scopingList.get(i).getNumericRegister(name);

         if (reg != null)
         {
            return reg;
         }
      }

      return globalSettings.getNumericRegister(name);
   }

   /**
    * Gets a named token register.
    * @param name the register's control sequence name
    * @throws TeXSyntaxException if a register is found with the
    * given name but is not a token register
    */
   public TokenRegister getTokenRegister(String name)
    throws TeXSyntaxException
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         TokenRegister reg = scopingList.get(i).getTokenRegister(name);

         if (reg != null)
         {
            return reg;
         }
      }

      return globalSettings.getTokenRegister(name);
   }

   /**
    * Gets a named register.
    * @param name the register's control sequence name
    * @return the register or null if not found
    */
   public Register getRegister(String name)
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         Register reg = scopingList.get(i).getRegister(name);

         if (reg != null)
         {
            return reg;
         }
      }

      return globalSettings.getRegister(name);
   }

   /**
    * Sets the contents of a register (locally or globally).
    * If global, all local copies of the register will be removed.
    * @param isLocal true if local assignment
    * @param name the register's control sequence name
    * @param value the value or content to assign to the register
    * @return the register that was changed
    */
   public Register setRegister(boolean isLocal, String name, TeXObject value)
    throws TeXSyntaxException
   {
      Register reg = null;

      if (isLocal && !scopingList.isEmpty())
      {
         TeXSettings settings = scopingList.lastElement();

         reg = settings.getRegister(name);

         if (reg == null)
         {
            for (int i = scopingList.size()-2; i >= 0; i--)
            {
               reg = scopingList.get(i).getRegister(name);

               if (reg != null)
               {
                  reg = (Register)reg.clone();
                  reg.setContents(parser, value);
                  getCurrentSettings().putRegister(reg);

                  break;
               }
            }

            if (reg == null)
            {
               reg = globalSettings.getRegister(name);

               if (reg == null)
               {
                  throw new TeXSyntaxException(parser,
                     TeXSyntaxException.ERROR_REGISTER_UNDEF, name);
               }

               reg = (Register)reg.clone();
               reg.setContents(parser, value);
               getCurrentSettings().putRegister(reg);
            }
         }
         else
         {
            reg.setContents(parser, value);
         }
      }
      else
      {
         reg = globalSettings.getRegister(name);

         for (int i = scopingList.size()-1; i >= 0; i--)
         {
            TeXSettings settings = scopingList.get(i);

            ControlSequence cs = settings.removeControlSequence(name);

            // Register should be globally defined, but just in case
            // it was just locally defined:

            if (reg == null && cs != null && cs instanceof Register)
            {
               reg = (Register)cs;

               globalSettings.putRegister(reg);
            }
         }

         if (reg == null)
         {
            throw new TeXSyntaxException(parser,
               TeXSyntaxException.ERROR_REGISTER_UNDEF, name);
         }

         reg.setContents(parser, value);
      }

      return reg;
   }

   /**
    * Sets the integer value of a register (locally or globally).
    * If global, all local copies of the register will be removed.
    * @param isLocal true if local assignment
    * @param name the register's control sequence name
    * @param value the numeric value to assign to the register
    * @return the register that was changed
    */
   public Register setRegister(boolean isLocal, String name, int value)
    throws TeXSyntaxException
   {
      Register reg = null;

      if (isLocal && !scopingList.isEmpty())
      {
         TeXSettings settings = scopingList.lastElement();

         reg = settings.getRegister(name);

         if (reg == null)
         {
            for (int i = scopingList.size()-2; i >= 0; i--)
            {
               reg = scopingList.get(i).getRegister(name);

               if (reg != null)
               {
                  reg = (Register)reg.clone();
                  getCurrentSettings().putRegister(reg);

                  break;
               }
            }

            if (reg == null)
            {
               reg = globalSettings.getRegister(name);

               if (reg != null)
               {
                  reg = (Register)reg.clone();
                  getCurrentSettings().putRegister(reg);
               }
            }
         }
      }
      else
      {
         reg = globalSettings.getRegister(name);

         for (int i = scopingList.size()-1; i >= 0; i--)
         {
            TeXSettings settings = scopingList.get(i);

            ControlSequence cs = settings.removeControlSequence(name);

            // Register should be globally defined, but just in case
            // it was just locally defined:

            if (reg == null && cs != null && cs instanceof Register)
            {
               reg = (Register)cs;

               globalSettings.putRegister(reg);
            }
         }
      }

      if (reg == null)
      {
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_REGISTER_UNDEF, name);
      }

      reg.setContents(parser, value);

      return reg;
   }

   /**
    * Sets the numeric value of a register (locally or globally).
    * If global, all local copies of the register will be removed.
    * @param isLocal true if local assignment
    * @param name the register's control sequence name
    * @param value the numeric value to assign to the register
    * @return the register that was changed
    */
   public Register setRegister(boolean isLocal, String name, Numerical value)
    throws TeXSyntaxException
   {
      Register reg = null;

      if (isLocal && !scopingList.isEmpty())
      {
         TeXSettings settings = scopingList.lastElement();

         reg = settings.getRegister(name);

         if (reg == null)
         {
            for (int i = scopingList.size()-2; i >= 0; i--)
            {
               reg = scopingList.get(i).getRegister(name);

               if (reg != null)
               {
                  reg = (Register)reg.clone();
                  getCurrentSettings().putRegister(reg);

                  break;
               }
            }

            if (reg == null)
            {
               reg = globalSettings.getRegister(name);

               if (reg != null)
               {
                  reg = (Register)reg.clone();
                  getCurrentSettings().putRegister(reg);
               }
            }
         }
      }
      else
      {
         reg = globalSettings.getRegister(name);

         for (int i = scopingList.size()-1; i >= 0; i--)
         {
            TeXSettings settings = scopingList.get(i);

            ControlSequence cs = settings.removeControlSequence(name);

            // Register should be globally defined, but just in case
            // it was just locally defined:

            if (reg == null && cs != null && cs instanceof Register)
            {
               reg = (Register)cs;

               globalSettings.putRegister(reg);
            }
         }
      }

      if (reg == null)
      {
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_REGISTER_UNDEF, name);
      }

      if (reg instanceof NumericRegister)
      {
         ((NumericRegister)reg).setValue(parser, value);
      }
      else if (value instanceof TeXObject)
      {
         reg.setContents(parser, (TeXObject)value);
      }
      else
      {
         reg.setContents(parser, (TeXObject)new UserNumber(value.number(parser)));
      }

      return reg;
   }

   public Register globalSetRegister(String name, Numerical value)
    throws TeXSyntaxException
   {
      return setRegister(false, name, value);
   }

   public Register localSetRegister(String name, Numerical value)
    throws TeXSyntaxException
   {
      return setRegister(true, name, value);
   }

   /**
    * Advances the numeric value of a register (locally or globally).
    * If global, all local copies of the register will be removed.
    * @param isLocal true if local assignment
    * @param name the register's control sequence name
    * @param value the numeric value to add to the register's
    * current value
    * @return the register that was changed
    */
   public NumericRegister advanceRegister(boolean isLocal, String name, Numerical value)
    throws TeXSyntaxException
   {
      Register reg = null;

      if (isLocal && !scopingList.isEmpty())
      {
         TeXSettings settings = scopingList.lastElement();

         reg = settings.getRegister(name);

         if (reg == null)
         {
            for (int i = scopingList.size()-2; i >= 0; i--)
            {
               reg = scopingList.get(i).getRegister(name);

               if (reg != null)
               {
                  reg = (Register)reg.clone();
                  getCurrentSettings().putRegister(reg);

                  break;
               }
            }

            if (reg == null)
            {
               reg = globalSettings.getRegister(name);

               if (reg != null)
               {
                  reg = (Register)reg.clone();
                  getCurrentSettings().putRegister(reg);
               }
            }
         }
      }
      else
      {
         reg = globalSettings.getRegister(name);

         for (int i = scopingList.size()-1; i >= 0; i--)
         {
            TeXSettings settings = scopingList.get(i);

            ControlSequence cs = settings.removeControlSequence(name);

            // Register should be globally defined, but just in case
            // it was just locally defined:

            if (reg == null && cs != null && cs instanceof Register)
            {
               reg = (Register)cs;

               globalSettings.putRegister(reg);
            }
         }
      }

      if (reg == null)
      {
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_REGISTER_UNDEF, name);
      }

      if (!(reg instanceof NumericRegister))
      {
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_NUMERIC_REGISTER_EXPECTED);
      }

      NumericRegister numReg = (NumericRegister)reg;

      numReg.advance(parser, value);

      return numReg;
   }

   public NumericRegister globalAdvanceRegister(String name, Numerical value)
    throws TeXSyntaxException
   {
      return advanceRegister(false, name, value);
   }

   public NumericRegister localAdvanceRegister(String name, Numerical value)
    throws TeXSyntaxException
   {
      return advanceRegister(true, name, value);
   }

   /**
    * Multiplies the numeric value of a register (locally or globally).
    * If global, all local copies of the register will be removed.
    * @param isLocal true if local assignment
    * @param name the register's control sequence name
    * @param value the numeric value by which to multiply the register's
    * current value
    * @return the register that was changed
    */
   public NumericRegister multiplyRegister(boolean isLocal, String name, Numerical value)
    throws TeXSyntaxException
   {
      Register reg = null;

      if (isLocal && !scopingList.isEmpty())
      {
         TeXSettings settings = scopingList.lastElement();

         reg = settings.getRegister(name);

         if (reg == null)
         {
            for (int i = scopingList.size()-2; i >= 0; i--)
            {
               reg = scopingList.get(i).getRegister(name);

               if (reg != null)
               {
                  reg = (Register)reg.clone();
                  getCurrentSettings().putRegister(reg);

                  break;
               }
            }

            if (reg == null)
            {
               reg = globalSettings.getRegister(name);

               if (reg != null)
               {
                  reg = (Register)reg.clone();
                  getCurrentSettings().putRegister(reg);
               }
            }
         }
      }
      else
      {
         reg = globalSettings.getRegister(name);

         for (int i = scopingList.size()-1; i >= 0; i--)
         {
            TeXSettings settings = scopingList.get(i);

            ControlSequence cs = settings.removeControlSequence(name);

            // Register should be globally defined, but just in case
            // it was just locally defined:

            if (reg == null && cs != null && cs instanceof Register)
            {
               reg = (Register)cs;

               globalSettings.putRegister(reg);
            }
         }
      }

      if (reg == null)
      {
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_REGISTER_UNDEF, name);
      }

      if (!(reg instanceof NumericRegister))
      {
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_NUMERIC_REGISTER_EXPECTED);
      }

      NumericRegister numReg = (NumericRegister)reg;

      numReg.multiply(value.number(parser));

      return numReg;
   }

   /**
    * Divides the numeric value of a register (locally or globally).
    * If global, all local copies of the register will be removed.
    * @param isLocal true if local assignment
    * @param name the register's control sequence name
    * @param value the numeric value by which to divide the register's
    * current value
    * @return the register that was changed
    */
   public NumericRegister divideRegister(boolean isLocal, String name, Numerical value)
    throws TeXSyntaxException
   {
      Register reg = null;

      if (isLocal && !scopingList.isEmpty())
      {
         TeXSettings settings = scopingList.lastElement();

         reg = settings.getRegister(name);

         if (reg == null)
         {
            for (int i = scopingList.size()-2; i >= 0; i--)
            {
               reg = scopingList.get(i).getRegister(name);

               if (reg != null)
               {
                  reg = (Register)reg.clone();
                  getCurrentSettings().putRegister(reg);

                  break;
               }
            }

            if (reg == null)
            {
               reg = globalSettings.getRegister(name);

               if (reg != null)
               {
                  reg = (Register)reg.clone();
                  getCurrentSettings().putRegister(reg);
               }
            }
         }
      }
      else
      {
         reg = globalSettings.getRegister(name);

         for (int i = scopingList.size()-1; i >= 0; i--)
         {
            TeXSettings settings = scopingList.get(i);

            ControlSequence cs = settings.removeControlSequence(name);

            // Register should be globally defined, but just in case
            // it was just locally defined:

            if (reg == null && cs != null && cs instanceof Register)
            {
               reg = (Register)cs;

               globalSettings.putRegister(reg);
            }
         }
      }

      if (reg == null)
      {
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_REGISTER_UNDEF, name);
      }

      if (!(reg instanceof NumericRegister))
      {
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_NUMERIC_REGISTER_EXPECTED);
      }

      NumericRegister numReg = (NumericRegister)reg;

      numReg.divide(value.number(parser));

      return numReg;
   }

   /**
    * Removes character from active character tables.
    * This method traverses the local list removing the character
    * from each scope and finally removes the character from the
    * global settings. The character's category code is also
    * cleared.
    * @param character the character identified by its code point
    * @return the first instance to be removed or null if the
    * character was not present in any table
    */
   public ActiveChar removeActiveChar(Integer character)
   {
      ActiveChar activeChar = null;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         TeXSettings settings = scopingList.get(i);

         ActiveChar ac = settings.removeActiveChar(character);

         if (activeChar == null && ac != null)
         {
            activeChar = ac;
         }
      }

      ActiveChar ac = globalSettings.removeActiveChar(character);

      if (activeChar == null && ac != null)
      {
         activeChar = ac;
      }

      return activeChar;
   }

   /**
    * Undefines active character.
    * If global, this method removes the active character from the
    * global settings and all local scopes and the character's
    * category code is cleared if it's set to active.
    * If local, this method removes the active character from the current
    * scope and locally changes the category code to letter or
    * other if it's currently set to active.
    */
   public void undefActiveChar(boolean isLocal, Integer character)
   {
      if (isLocal)
      {
         CategoryCode catCode = getCategoryCode(character);
         getCurrentSettings().removeActiveChar(character);

         if (catCode == CategoryCode.ACTIVE)
         {
            if (Character.isAlphabetic(character.intValue()))
            {
               catCode = CategoryCode.LETTER;
            }
            else if (Character.isWhitespace(character.intValue()))
            {
               catCode = CategoryCode.SPACE;
            }
            else
            {
               catCode = CategoryCode.OTHER;
            }

            setCategoryCode(isLocal, character, catCode);
         }
      }
      else
      {
         removeActiveChar(character);
      }
   }

   /**
    * Gets the active character identified by its code point.
    * @param character the character's code point
    * @return the active character or null if not present 
    */
   public ActiveChar getActiveChar(Integer character)
   {
      ActiveChar ac = null;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         ac = scopingList.get(i).getActiveChar(character);

         if (ac != null)
         {
            return ac;
         }
      }

      return globalSettings.getActiveChar(character);
   }

   /**
    * Defines an active character locally or globally.
    * If global, the active character is added to the global
    * settings. If local, the active character is added to the
    * current settings. The character's category code is also set to
    * active.
    * @param isLocal true if local
    * @param activeChar the active character
    */
   public void putActiveChar(boolean isLocal, ActiveChar activeChar)
   {
      if (isLocal)
      {
         getCurrentSettings().putActiveChar(activeChar);
      }
      else
      {
         Integer character = Integer.valueOf(activeChar.getCharCode());

         for (int i = scopingList.size()-1; i >= 0; i--)
         {
            TeXSettings settings = scopingList.get(i);

            settings.removeActiveChar(character);
         }

         globalSettings.putActiveChar(activeChar);
      }
   }

   /**
    * Gets the first character with the given category code assigned
    * to it.
    * @param catCode the category code
    * @param defValue the default value
    * @return the character code point or the default value if none set
    */
   public int getSpecialChar(int catCode, int defValue)
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         int cp = scopingList.get(i).getSpecialChar(catCode);

         if (cp != -1)
         {
            return cp;
         }
      }

      int cp = globalSettings.getSpecialChar(catCode);

      return cp == -1 ? defValue : cp;
   }

   /**
    * Indicates whether or not category codes should be ignored.
    * If true, all characters should be either letter or other.
    * @return true if category codes should be ignored
    */
   public boolean isDetokenizing()
   {
      return getCurrentSettings().isDetokenizing();
   }

   public void setDetokenizing(boolean on)
   {
      getCurrentSettings().setDetokenizing(on);
   }

   /**
    * Sets the category code for the given character.
    * @param isLocal true if the assignment is local to the current
    * scope
    * @param codePoint the character's code point
    * @param catCode the category code to assign to the character
    */
   public void setCategoryCode(boolean isLocal, int codePoint, CategoryCode catCode)
   {
      setCategoryCode(isLocal, Integer.valueOf(codePoint), catCode);
   }

   /**
    * Sets the category code for the given character.
    * @param isLocal true if the assignment is local to the current
    * scope
    * @param character the character's code point
    * @param catCode the category code to assign to the character
    */
   public void setCategoryCode(boolean isLocal, Integer character, CategoryCode catCode)
   {
      if (isLocal)
      {
         if (parser.isDebugMode(TeXParser.DEBUG_CATCODE))
         {
            parser.logMessage(String.format("CatCode (local) %s -> %d",
             new String(Character.toChars(character.intValue())), catCode.getId()));
         }

         getCurrentSettings().setCategoryCode(character, catCode);
      }
      else
      {
         if (parser.isDebugMode(TeXParser.DEBUG_CATCODE))
         {
            parser.logMessage(String.format("CatCode (global) %s -> %d",
             new String(Character.toChars(character.intValue())), catCode.getId()));
         }

         for (TeXSettings settings : scopingList)
         {
            settings.clearCodePointCategoryCode(character);
         }

         globalSettings.setCategoryCode(character, catCode);
      }
   }

   /**
    * Gets the category code for the given character.
    * @param codePoint the character's code point
    * @return the category code
    */
   public CategoryCode getCategoryCode(int codePoint)
   {
      return getCategoryCode(Integer.valueOf(codePoint));
   }

   /**
    * Gets the category code for the given character.
    * @param character the character's code point
    * @return the category code
    */
   public CategoryCode getCategoryCode(Integer character)
   {
      CategoryCode catCode = null;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         TeXSettings settings = scopingList.get(i);

         catCode = settings.getCategoryCode(character);

         if (catCode != null) break;
      }

      if (catCode == null)
      {
         catCode = globalSettings.getCategoryCode(character);
      }

      if (
           catCode != null
           && !(catCode == CategoryCode.BG || catCode == CategoryCode.EG)
           && isDetokenizing()
         )
      {
         catCode = null;
      }

      if (catCode == null)
      {
         if (Character.isAlphabetic(character.intValue()))
         {
            catCode = CategoryCode.LETTER;
         }
         else if (Character.isWhitespace(character.intValue()))
         {
            catCode = CategoryCode.SPACE;
         }
         else
         {
            catCode = CategoryCode.OTHER;
         }
      }

      return catCode;
   }

   /**
    * Tests if the current font family is <code>TeXFontFamily.VERB</code>.
    * @return true if in verbatim or pseudo-verbatim mode
    */
   public boolean inVerbatim()
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         Boolean inVerb = scopingList.get(i).inVerbatim();

         if (inVerb != null)
         {
            return inVerb.booleanValue();
         }
      }

      Boolean inVerb = globalSettings.inVerbatim();

      return inVerb == null ? false : inVerb.booleanValue();
   }

   /**
    * Sets the current mode.
    * @param newMode the new mode
    */
   public void setMode(TeXMode newMode)
   {
      getCurrentSettings().setMode(newMode);
   }

   /**
    * Gets the current mode.
    * @return the current mode
    */
   public TeXMode getMode()
   {
      TeXMode mode;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         mode = scopingList.get(i).getCurrentMode();

         if (mode != TeXMode.INHERIT)
         {
            return mode;
         }
      }

      mode = globalSettings.getCurrentMode();

      return mode == TeXMode.INHERIT ? TeXMode.TEXT : mode;
   }

   /**
    * Define a global count register with a specific allocation number.
    * @param name the register's control sequence name
    * @param alloc the register allocation
    * @return the new count register
    */
   public CountRegister countdef(String name, int alloc)
   {
      return globalSettings.countdef(name, alloc);
   }

   /**
    * Defines a global count register with an initial value.
    * Registers are all globally defined in TeX but local
    * definitions are allowed here.
    * @param name the register's control sequence name
    * @param value the register's initial value
    * @return the new count register
    */
   public CountRegister newcount(String name, int value)
   {
      return globalSettings.newcount(name, value);
   }

   /**
    * Defines a count register with an initial value.
    * Registers are all globally defined in TeX but local
    * definitions are allowed here.
    * @param isLocal true if local definition
    * @param name the register's control sequence name
    * @param value the register's initial value
    * @return the new count register
    */
   public CountRegister newcount(boolean isLocal, String name, int value)
   {
      if (isLocal)
      {
         return getCurrentSettings().newcount(name, value);
      }
      else
      {
         return globalSettings.newcount(name, value);
      }
   }

   /**
    * Defines a global count register.
    * Registers are all globally defined in TeX but local
    * definitions are allowed here.
    * @param name the register's control sequence name
    * @return the new count register
    */
   public CountRegister newcount(String name)
   {
      return globalSettings.newcount(name);
   }

   /**
    * Defines a count register.
    * Registers are all globally defined in TeX but local
    * definitions are allowed here.
    * @param isLocal true if local definition
    * @param name the register's control sequence name
    * @return the new count register
    */
   public CountRegister newcount(boolean isLocal, String name)
   {
      if (isLocal)
      {
         return getCurrentSettings().newcount(name);
      }
      else
      {
         return globalSettings.newcount(name);
      }
   }

   /**
    * Define a dimension register with a specific allocation number.
    * Registers are all globally defined.
    * @param name the register's control sequence name
    * @param alloc the register allocation
    * @return the new dimension register
    */
   public DimenRegister dimendef(String name, int alloc)
   {
      return globalSettings.dimendef(name, alloc);
   }

   /**
    * Defines a dimension register.
    * Registers are all globally defined in TeX but local
    * definitions are allowed here.
    * @param name the register's control sequence name
    * @return the new dimension register
    */
   public DimenRegister newdimen(String name)
   {
      return globalSettings.newdimen(name);
   }

   /**
    * Defines a dimension register.
    * Registers are all globally defined in TeX but local
    * definitions are allowed here.
    * @param isLocal true if local definition
    * @param name the register's control sequence name
    * @return the new dimension register
    */
   public DimenRegister newdimen(boolean isLocal, String name)
   {
      if (isLocal)
      {
         return getCurrentSettings().newdimen(name);
      }
      else
      {
         return globalSettings.newdimen(name);
      }
   }

   /**
    * Define a token register with a specific allocation number.
    * Registers are all globally defined.
    * @param name the register's control sequence name
    * @param alloc the register allocation
    * @return the new token register
    */
   public TokenRegister toksdef(String name, int alloc)
   {
      return globalSettings.toksdef(name, alloc);
   }

   /**
    * Defines a global token register.
    * Registers are all globally defined in TeX but local
    * definitions are allowed here.
    * @param name the register's control sequence name
    * @return the new token register
    */
   public TokenRegister newtoks(String name)
   {
      return globalSettings.newtoks(name);
   }

   /**
    * Defines a token register.
    * Registers are all globally defined in TeX but local
    * definitions are allowed here.
    * @param isLocal true if local definition
    * @param name the register's control sequence name
    * @return the new token register
    */
   public TokenRegister newtoks(boolean isLocal, String name)
   {
      if (isLocal)
      {
         return getCurrentSettings().newtoks(name);
      }
      else
      {
         return globalSettings.newtoks(name);
      }
   }

   /**
    * Gets the math font.
    * @return the math font
    */
   public TeXFontMath getMathFont()
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         TeXFontMath font = scopingList.get(i).getCurrentMathFont();

         if (font != TeXFontMath.INHERIT)
         {
            return font;
         }
      }

      TeXFontMath font = globalSettings.getCurrentMathFont();

      return font == TeXFontMath.INHERIT ? TeXFontMath.NORMAL : font;
   }

   /**
    * Sets the math font for this scope.
    * @param setting the math font setting
    */
   public void setMathFont(TeXFontMath setting)
   {
      getCurrentSettings().setMathFont(setting);
   }

   /**
    * Checks if the current math font is bold.
    * Bold in this context means {@link TeXFont.Math.BF}, 
    * {@link TeXFont.BOLDSYMBOL} or {@link TeXFont.PMB}.
    * @return true if the math font is current a bold type
    */
   public boolean isMathBold()
   {
      Boolean result = null;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         result = scopingList.get(i).isMathBoldFont();

         if (result != null)
         {
            break;
         }
      }

      if (result == null)
      {
         result = globalSettings.isMathBoldFont();
      }

      return result == null ? false : result.booleanValue();
   }

   /**
    * Checks if the current math font is sans-serif.
    * @return true if the math font is sans-serif
    * or false otherwise
    */
   public boolean isMathSf()
   {
      Boolean result = null;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         result = scopingList.get(i).isMathSfFont();

         if (result != null)
         {
            break;
         }
      }

      if (result == null)
      {
         result = globalSettings.isMathSfFont();
      }

      return result == null ? false : result.booleanValue();
   }

   /**
    * Checks if the current math font is serif (Roman).
    * @return true if the math font is serif
    * or false otherwise
    */
   public boolean isMathRm()
   {
      Boolean result = null;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         result = scopingList.get(i).isMathRmFont();

         if (result != null)
         {
            break;
         }
      }

      if (result == null)
      {
         result = globalSettings.isMathRmFont();
      }

      return result == null ? false : result.booleanValue();
   }

   /**
    * Checks if the current math font is monospaced (typewriter).
    * @return true if the math font is monospaced
    * or false otherwise
    */
   public boolean isMathTt()
   {
      Boolean result = null;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         result = scopingList.get(i).isMathTtFont();

         if (result != null)
         {
            break;
         }
      }

      if (result == null)
      {
         result = globalSettings.isMathTtFont();
      }

      return result == null ? false : result.booleanValue();
   }

   /**
    * Checks if the current math font is calligraphic.
    * @return true if the math font is calligraphic
    * or false otherwise
    */
   public boolean isMathCal()
   {
      Boolean result = null;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         result = scopingList.get(i).isMathCalFont();

         if (result != null)
         {
            break;
         }
      }

      if (result == null)
      {
         result = globalSettings.isMathCalFont();
      }

      return result == null ? false : result.booleanValue();
   }

   /**
    * Checks if the current math font is frak.
    * @return true if the math font is frak
    * or false otherwise
    */
   public boolean isMathFrak()
   {
      Boolean result = null;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         result = scopingList.get(i).isMathFrakFont();

         if (result != null)
         {
            break;
         }
      }

      if (result == null)
      {
         result = globalSettings.isMathFrakFont();
      }

      return result == null ? false : result.booleanValue();
   }

   /**
    * Checks if the current math font is blackboard bold.
    * @return true if the math font is blackboard bold
    * or false otherwise
    */
   public boolean isMathBb()
   {
      Boolean result = null;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         result = scopingList.get(i).isMathBbFont();

         if (result != null)
         {
            break;
         }
      }

      if (result == null)
      {
         result = globalSettings.isMathBbFont();
      }

      return result == null ? false : result.booleanValue();
   }

   /**
    * Checks if the current math font is italic.
    * @return true if the math font is italic
    * or false otherwise
    */
   public boolean isMathIt()
   {
      Boolean result = null;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         result = scopingList.get(i).isMathItFont();

         if (result != null)
         {
            break;
         }
      }

      if (result == null)
      {
         result = globalSettings.isMathItFont();
      }

      return result == null ? true : result.booleanValue();
   }

   /**
    * Checks if the current text font is sans-serif.
    * @return true if the text font is sans-serif
    * or false otherwise
    */
   public boolean isTextSansSerif()
   {
      Boolean result = null;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         result = scopingList.get(i).isTextSansSerifFont();

         if (result != null)
         {
            break;
         }
      }

      if (result == null)
      {
         result = globalSettings.isTextSansSerifFont();
      }

      return result == null ? false : result.booleanValue();
   }

   /**
    * Sets the font.
    * Each font attribute (family, shape, weight and size) is set if not null.
    * @param font the font
    */
   public void setFont(TeXFontText font)
   {
      getCurrentSettings().setFont(font);
   }

   /**   
    * Sets the font family.
    * @param setting the font family setting
    */
   public void setFontFamily(TeXFontFamily setting)
   {
      getCurrentSettings().setFontFamily(setting);
   }

   /**
    * Sets the font shape.
    * @param setting the font shape setting
    */
   public void setFontShape(TeXFontShape setting)
   {
      getCurrentSettings().setFontShape(setting);
   }

   /**
    * Sets the font weight.
    * @param setting the font weight setting
    */
   public void setFontWeight(TeXFontWeight setting)
   {
      getCurrentSettings().setFontWeight(setting);
   }

   /**
    * Sets user font size.
    * For use where an explicit size is requested rather than
    * predefined relative sizes, such as
    * <code>TeXFontSize.NORMAL</code> or
    * <code>TeXFontSize.LARGE</code>. This automatically sets the current font
    * size to <code>TeXFontSize.USER</code>.
    * @param dim the font size dimension
    */
   public void setUserFontSize(TeXDimension dim)
   {
      getCurrentSettings().setUserFontSize(dim);
   }

   /**
    * Sets the font size.
    * Note that <code>TeXFontSize.USER</code> requires the actual
    * size to be provided with {@link #setUserFontSize(TeXDimension)}.
    * @param setting the font size setting
    */
   public void setFontSize(TeXFontSize setting)
   {
      getCurrentSettings().setFontSize(setting);
   }

   /**
    * Gets the font family.
    * @return the font family 
    */
   public TeXFontFamily getFontFamily()
   {
      TeXFontFamily family = TeXFontFamily.INHERIT;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         family = scopingList.get(i).getCurrentFontFamily();

         if (family != TeXFontFamily.INHERIT)
         {
            return family;
         }
      }

      family = globalSettings.getCurrentFontFamily();

      return family == TeXFontFamily.INHERIT ? TeXFontFamily.RM : family;
   }

   public boolean isTextItalicOrSlanted()
   {
      TeXFontShape shape = TeXFontShape.INHERIT;
      boolean em = false;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         shape = scopingList.get(i).getCurrentFontShape();

         switch (shape)
         {
            case IT:
            case SL:
               return !em;
            case EM:
               em = true;
            break;
            case INHERIT:
            break;
            default:
              return em;
         }
      }

      shape = globalSettings.getCurrentFontShape();

      switch (shape)
      {
         case IT:
         case SL:
         case EM:
           return !em;
         default:
           return em;
      }
   }

   /**
    * Gets the font shape.
    * @return the font shape 
    */
   public TeXFontShape getFontShape()
   {
      TeXFontShape shape = TeXFontShape.INHERIT;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         shape = scopingList.get(i).getCurrentFontShape();

         if (shape != TeXFontShape.INHERIT)
         {
            return shape;
         }
      }

      shape = globalSettings.getCurrentFontShape();

      return shape == TeXFontShape.INHERIT ? TeXFontShape.UP : shape;
   }

   /**
    * Checks if the current text font is bold.
    * @return true if the text font is bold
    * or false otherwise
    */
   public boolean isTextBold()
   {
      TeXFontWeight weight = TeXFontWeight.INHERIT;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         weight = scopingList.get(i).getCurrentFontWeight();

         switch (weight)
         {
            case BF:
            case STRONG:
               return true;
            case INHERIT:
            break;
            default: return false;
         }
      }

      weight = globalSettings.getCurrentFontWeight();

      switch (weight)
      {
         case BF:
         case STRONG:
            return true;
         default: return false;
      }
   }

   /**
    * Gets the font weight.
    * @return the font weight 
    */
   public TeXFontWeight getFontWeight()
   {
      TeXFontWeight weight = TeXFontWeight.INHERIT;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         weight = scopingList.get(i).getCurrentFontWeight();

         if (weight != TeXFontWeight.INHERIT)
         {
            return weight;
         }
      }

      weight = globalSettings.getCurrentFontWeight();

      return weight == TeXFontWeight.INHERIT ? TeXFontWeight.MD : weight;
   }

   /**
    * Gets the font size.
    * @return the font size 
    */
   public TeXFontSize getFontSize()
   {
      TeXFontSize size = TeXFontSize.INHERIT;

      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         size = scopingList.get(i).getCurrentFontSize();

         if (size != TeXFontSize.INHERIT)
         {
            return size;
         }
      }

      size = globalSettings.getCurrentFontSize();

      return size == TeXFontSize.INHERIT ? TeXFontSize.NORMAL : size;
   }

   /**
    * Gets the current font encoding.
    * @return the current font encoding or null if not set
    */
   public FontEncoding getFontEncoding()
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         FontEncoding enc = scopingList.get(i).getCurrentFontEncoding();

         if (enc != null) return enc;
      }

      return globalSettings.getCurrentFontEncoding();
   }

   /**
    * Sets the font encoding.
    * @param fontenc the font encoding
    */
   public void setFontEncoding(FontEncoding fontenc)
   {
      getCurrentSettings().setFontEncoding(fontenc);
   }

   /**
    * Gets the character mapping mode.
    * @return the character mapping mode
    */
   public int getCharMapMode()
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         int mode = scopingList.get(i).getCurrentCharMapMode();

         if (mode != TeXSettings.INHERIT)
         {
            return mode;
         }
      }

      int mode = globalSettings.getCurrentCharMapMode();

      return mode == TeXSettings.INHERIT ? TeXSettings.CHAR_MAP_OFF : mode;
   }

   /**
    * Sets the character mapping mode for the current scope.
    * @param setting the mapping, which may be one of
    * {@link TeXSettings.INHERIT},
    * {@link TeXSettings.CHAR_MAP_ON}, or
    * {@link TeXSettings.CHAR_MAP_OFF}.
    * @throws IllegalArgumentException if the setting is invalid
    */
   public void setCharMapMode(int setting)
   throws IllegalArgumentException
   {
      getCurrentSettings().setCharMapMode(setting);
   }

   /**
    * Gets the character obtained from a code according to the
    * current mode and font encoding. This method also performs conversions
    * such as straight apostrophe to closing single quote.
    * @param charCode the character code
    * @return the mapped character
    */
   public int getCharCode(int charCode)
   {
      FontEncoding fontEncoding = getFontEncoding();

      if (fontEncoding != null)
      {        
         int mappedCharCode = fontEncoding.getCharCode(charCode);
            
         if (mappedCharCode != FontEncoding.CHAR_MAP_NONE)
         {     
            return mappedCharCode;
         }     
      } 

      if (getCharMapMode() == TeXSettings.CHAR_MAP_OFF)
      {           
         return FontEncoding.CHAR_MAP_NONE;
      }

      switch (getMode())
      {
         case TEXT :
            
           if (charCode == (int)'\'')
           {
              return 0x2019;
           }
            
           return FontEncoding.CHAR_MAP_NONE;
               
         case INLINE_MATH:
         case DISPLAY_MATH:

            if (charCode == (int)'-')
            {
               charCode = 0x2212;
            }     
            else if (charCode == (int)'/')
            {     
               charCode = 0x2215;
            }
               
            if (isMathBb())
            {
               return TeXSettings.getCode(charCode, TeXSettings.BLACKBOARD_BOLD);
            }
               
            if (isMathFrak())
            {
               if (isMathBold())
               {
                  return TeXSettings.getCode(charCode, TeXSettings.BLACKBOARD_BOLD);
               }
            
               return TeXSettings.getCode(charCode, TeXSettings.FRAKTUR);
            }  
    
            if (isMathCal())
            {
               if (isMathBold())
               {
                  return TeXSettings.getCode(charCode, TeXSettings.MATHCALSCRIPT_BOLD);
               }

               return TeXSettings.getCode(charCode, TeXSettings.MATHCALSCRIPT);
            }
            if (isMathIt())
            {
               if (isMathBold())
               {
                  if (isMathSf())
                  {
                     return TeXSettings.getCode(charCode, TeXSettings.MATH_IT_BOLD_SF);
                  }

                  if (!isMathTt())
                  {
                     return TeXSettings.getCode(charCode, TeXSettings.MATH_IT_BOLD);
                  }
               }

               if (isMathSf())
               {
                  return TeXSettings.getCode(charCode, TeXSettings.MATH_IT_SF);
               }

               if (isMathTt())
               {
                  return TeXSettings.getCode(charCode, TeXSettings.MATH_IT);
               }
            }

            if (isMathBold())
            {
               return TeXSettings.getCode(charCode, TeXSettings.MATH_UP_BOLD);
            }

      }

      return FontEncoding.CHAR_MAP_NONE;
   }

   /**
    * Gets the character string corresponding to the given code for
    * the current font encoding.
    * @param charCode the character code
    * @return the string obtained from the font encoding mapping
    */
   public String getCharString(int charCode)
   {
      int mappedCode = getCharCode(charCode);

      if (mappedCode == FontEncoding.CHAR_MAP_COMPOUND)
      {
         FontEncoding fontEncoding = getFontEncoding();

         if (fontEncoding != null)
         { 
            return fontEncoding.getCharString(charCode);
         } 
      }        

      if (mappedCode == FontEncoding.CHAR_MAP_NONE)
      {
         return new String(Character.toChars(charCode));
      }
      else
      {     
         return new String(Character.toChars(mappedCode));
      } 
   }

   /**
    * Gets the font dimension.
    * @return the font dimension
    */ 
   public TeXDimension getFontDimension()
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         TeXDimension dim = scopingList.get(i).getCurrentFontDimension();

         if (dim != null)
         {
            return dim;
         }
      }

      return globalSettings.getCurrentFontDimension();
   }

   /**
    * Gets the paragraph alignment.
    * @return the paragraph alignment
    */
   public int getParAlign()
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         int align = scopingList.get(i).getCurrentParAlign();

         if (align != TeXSettings.INHERIT)
         {
            return align;
         }
      }

      int align = globalSettings.getCurrentParAlign();

      return align == TeXSettings.INHERIT ? TeXSettings.PAR_ALIGN_NORMAL : align;
   }

   /**
    * Sets the paragraph alignment for this scope.
    * The setting should be one of:
    * <code>TeXSettings.INHERIT</code>,
    * <code>TeXSettings.PAR_ALIGN_NORMAL</code>,
    * <code>TeXSettings.PAR_ALIGN_LEFT</code>,
    * <code>TeXSettings.PAR_ALIGN_RIGHT</code>, or
    * <code>TeXSettings.PAR_ALIGN_CENTER</code>.
    * @param setting the alignment setting
    * @throws IllegalArgumentException if the setting is not a
    * recognised value
    */
   public void setParAlign(int setting)
   throws IllegalArgumentException
   {
      getCurrentSettings().setParAlign(setting);
   }

   /**
    * Gets the paragraph indentation.
    * @return the paragraph indentation or null if not set
    */
   public TeXDimension getParIndent()
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         TeXDimension dim = scopingList.get(i).getCurrentParIndent();

         if (dim != null)
         {
            return dim;
         }
      }

      return globalSettings.getCurrentParIndent();
   }

   /**
    * Sets the paragraph indentation.
    * @param indent the paragraph indentation
    */
   public void setParIndent(TeXDimension indent)
   {
      getCurrentSettings().setParIndent(indent);
   }

   /**
    * Gets the paragraph skip.
    * @return the paragraph skip or null if not set
    */
   public TeXDimension getParSkip()
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         TeXDimension dim = scopingList.get(i).getCurrentParSkip();

         if (dim != null)
         {
            return dim;
         }
      }

      return globalSettings.getCurrentParSkip();
   }

   /**
    * Sets the paragraph skip.
    * @param dim the paragraph skip
    */
   public void setParSkip(TeXDimension dim)
   {
      getCurrentSettings().setParSkip(dim);
   }

   /**
    * Gets the hanging indent.
    * @return the hanging indent or null if not set
    */
   public TeXDimension getHangIndent()
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         TeXDimension dim = scopingList.get(i).getCurrentHangIndent();

         if (dim != null)
         {
            return dim;
         }
      }

      return globalSettings.getCurrentHangIndent();
   }

   /**
    * Sets the hanging indent.
    * @param indent the hanging indent
    */
   public void setHangIndent(TeXDimension indent)
   {
      getCurrentSettings().setHangIndent(indent);
   }

   /**
    * Gets the alignment mode.
    * The return value will be one of:
    * {@link TeXSettings.ALIGN_MODE_FALSE} or
    * {@link TeXSettings.ALIGN_MODE_TRUE}
    * @return the alignment mode
    */
   public int getAlignMode()
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         int align = scopingList.get(i).getCurrentAlignMode();

         if (align != TeXSettings.INHERIT)
         {
            return align;
         }
      }

      int align = globalSettings.getCurrentAlignMode();

      return align == TeXSettings.INHERIT ? TeXSettings.ALIGN_MODE_FALSE : align;
   }

   /**
    * Sets the current alignment mode.
    * @param setting the current align mode, which may be one of:
    * {@link TeXSettings.ALIGN_MODE_FALSE},
    * {@link TeXSettings.ALIGN_MODE_TRUE} or
    * {@link TeXSettings.INHERIT}
    * @throws IllegalArgumentException if the setting is invalid
    */
   public void setAlignMode(int setting)
    throws IllegalArgumentException
   {
      getCurrentSettings().setAlignMode(setting);
   }

   /**
    * Starts tabular alignment.
    */
   public void startAlignment()
   {
      getCurrentSettings().startAlignment();
   }

   /**
    * Ends tabular alignment.
    */
   public void endAlignment()
   {
      getCurrentSettings().endAlignment();
   }

   /**
    * Starts a tabular row.
    */
   public void startRow()
   {
      getCurrentSettings().startRow();
   }

   /**
    * Starts a tabular column.
    */
   public void startColumn()
   {
      getCurrentSettings().startColumn();
   }

   /**
    * Gets tabular start row mode.
    * @return the tabular start row mode
    */
   public int getStartRowMode()
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         int mode = scopingList.get(i).getCurrentStartRowMode();

         if (mode != TeXSettings.INHERIT)
         {
            return mode;
         }
      }

      int mode = globalSettings.getCurrentStartRowMode();

      return mode == TeXSettings.INHERIT ? TeXSettings.START_ROW_MODE_FALSE : mode;
   }

   /**
    * Sets the start row mode for this scope.
    * The setting may be one of:
    * {@link TeXSettings.START_ROW_MODE_FALSE},
    * {@link TeXSettings.START_ROW_MODE_TRUE} or
    * {@link TeXSettings.INHERIT}
    * @param setting the start row mode
    * @throws IllegalArgumentException if the setting is invalid
    */
   public void setStartRowMode(int setting)
   throws IllegalArgumentException
   {
      getCurrentSettings().setStartRowMode(setting);
   }

   /**
    * Gets tabular start column mode.
    * @return the tabular start column mode
    */
   public int getStartColumnMode()
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         int mode = scopingList.get(i).getCurrentStartColumnMode();

         if (mode != TeXSettings.INHERIT)
         {
            return mode;
         }
      }

      int mode = globalSettings.getCurrentStartColumnMode();

      return mode == TeXSettings.INHERIT ? TeXSettings.START_COLUMN_MODE_FALSE : mode;
   }

   /**
    * Sets the start column mode for this scope.
    * The setting may be one of:
    * {@link TeXSettings.START_COLUMN_MODE_FALSE},
    * {@link TeXSettings.START_COLUMN_MODE_TRUE} or
    * {@link TeXSettings.INHERIT}
    * @param setting the start column mode
    * @throws IllegalArgumentException if the setting is invalid
    */
   public void setStartColumnMode(int setting)
   {
      getCurrentSettings().setStartColumnMode(setting);
   }

   /**
    * Gets the alignment column.
    * @return the alignment column 
    */
   public int getAlignmentColumn()
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         int column = scopingList.get(i).getCurrentAlignmentColumn();

         if (column != TeXSettings.INHERIT)
         {
            return column;
         }
      }

      int column = globalSettings.getCurrentAlignmentColumn();

      return column == TeXSettings.INHERIT ? 0 : column;
   }

   /**
    * Gets the alignment list.
    * @return the alignment list
    */
   public TeXCellAlignList getAlignmentList()
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         TeXCellAlignList list = scopingList.get(i).getCurrentAlignmentList();

         if (list != null)
         {
            return list;
         }
      }

      return globalSettings.getCurrentAlignmentList();
   }

   /**
    * Sets the alignment list.
    * @param list the alignment list
    */
   public void setAlignmentList(TeXCellAlignList list)
   {
      getCurrentSettings().setAlignmentList(list);
   }

   /**
    * Gets the alignment column count.
    * @param the alignment column count or 0 if not set
    */
   public int getAlignmentColumnCount()
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         int count = scopingList.get(i).getCurrentAlignmentColumnCount();

         if (count > 0)
         {
            return count;
         }
      }

      return globalSettings.getCurrentAlignmentColumnCount();
   }

   /**
    * Gets the foreground.
    * @return the foreground or <code>java.awt.Color.BLACK</code> if not set
    */
   public Color getFgColor()
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         Color col = scopingList.get(i).getCurrentFgColor();

         if (col != null)
         {
            return col;
         }
      }

      Color col = globalSettings.getCurrentFgColor();

      return col == null ? Color.BLACK : col;
   }

   /**
    * Sets the foreground.
    * @param col the foreground
    */
   public void setFgColor(Color col)
   {
      getCurrentSettings().setFgColor(col);
   }

   /**
    * Gets the background.
    * @return the background or null if not set
    */
   public Color getBgColor()
   {
      for (int i = scopingList.size()-1; i >= 0; i--)
      {
         Color col = scopingList.get(i).getCurrentBgColor();

         if (col != null)
         {
            return col;
         }
      }

      return globalSettings.getCurrentFgColor();
   }

   /**
    * Sets the background.
    * @param col the background
    */
   public void setBgColor(Color col)
   {
      getCurrentSettings().setBgColor(col);
   }

   Vector<TeXSettings> scopingList;
   TeXParser parser;
   TeXSettings globalSettings;
}
