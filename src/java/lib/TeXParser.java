/*
    Copyright (C) 2013-2023 Nicola L.C. Talbot
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

import java.util.Iterator;
import java.util.Vector;
import java.util.Hashtable;
import java.util.regex.Pattern;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.io.*;
import java.nio.file.Path;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.file.Files;

import com.dickimawbooks.texparserlib.latex.LaTeXSyntaxException;
import com.dickimawbooks.texparserlib.primitives.Undefined;

/**
 * The principle class in this library that deals with reading input
 * from a reader (file or string). It doesn't parse in quite the
 * same way as TeX but does recognise catcodes. The characters read
 * from the reader are converted into objects of type TeXObject.
 * These are either processed directly or are pushed onto the stack
 * to be processed after some action. Sometimes sub-stacks are
 * formed that need to be processed before returning to the main
 * parser stack.
 * 
 * Each stack is a TeXObjectList with the TeXParser as the main
 * stack. When the TeXParser stack runs out, more characters are
 * read from the reader until EOF. When sub-stacks run out they are
 * discarded and the main processing returns to the TeXParser stack.
 *
 * Be careful about pushing content to a stack or the parser as it
 * can delay processing the content.
 *
 * The principle TeXObject sub-classes are:
 *
 * Macro: corresponds to a macro, which may be ActiveChar (an active
 * character) or a ControlSequence (a control sequence). Some macros
 * may implement Expandable, which means they may be able to expand.
 * If they can expand, their expansion will be returned as a stack
 * otherwise null is returned. They may well expand differently to
 * the corresponding TeX macros. For example, they may not expand
 * even though their TeX definition may be expandable. Conversely,
 * they may expand, even though their TeX definition may be robust
 * (such as case-changing commands).
 *
 * CharObject: corresponds to a character, which may be a Letter or
 * Other.
 *
 * WhiteSpace: corresponds to a space character, which may be Space
 * or Eol.
 *
 * Par: corresponds to a paragraph break (multiple blank lines)
 *
 * ParameterToken: corresponds to a parameter marker, which may be
 * Param (e.g #1) or DoubleParam (e.g. ##1, ###1, ####1).
 *
 * Group: a form of stack that is treated as a single unit and
 * causes a new TeXSettings object to be created that represents the
 * local scope. The TeXSettings object is discarded when the group
 * ends.
 *
 * MathGroup: a sub-class of Group that switches to math-mode
 *
 * Special characters (SbChar, SpChar and Tab)
 *
 * Ignoreable: comments, spaces following control words. These
 * aren't automatically discarded to allow the latex2latex library
 * to retain comments in the output files. They are discarded when
 * popping arguments off the stack.
 *
 * Some information isn't available, such as where TeX breaks lines
 * and pages or font information.
 * 
 */
public class TeXParser extends TeXObjectList
{
   public TeXParser(TeXParserListener listener)
   {
      this.listener = listener;
      reader = null;

      activeTable = new Hashtable<Integer,ActiveChar>();
      csTable = new Hashtable<String,ControlSequence>();

      settings = new TeXSettings(this);

      verbatim = new Vector<String>();

      verbatim.add("verb");

      initDefCatCodes();
      initRegisters();

      listener.setParser(this);
   }

   private TeXParser()
   {
   }

   @Override
   public TeXObjectList createList()
   {
      return new TeXParser(listener);
   }

   private void initRegisters()
   {
      CountRegister countReg = new CountRegister("count@");
      allocCount(255, countReg);

      DimenRegister dimenReg = new DimenRegister("dimen@");
      allocDimen(0, dimenReg);

      dimenReg = new DimenRegister("dimen@i");
      allocDimen(1, dimenReg);
      dimenReg = new DimenRegister("dimen@ii");
      allocDimen(2, dimenReg);

      for (int i=0; i < 10; i++)
      {
         countReg = new CountRegister("count"+i);
         settings.putRegister(countReg);
         allocCount(ALLOC_COUNT, countReg);
      }

      countReg = new CountRegister("count"+ALLOC_COUNT, 22);
      settings.putRegister(countReg);
      allocCount(ALLOC_COUNT, countReg);

      countReg = new CountRegister("count"+ALLOC_DIMEN, 9);
      settings.putRegister(countReg);
      allocCount(ALLOC_DIMEN, countReg);

      countReg = new CountRegister("count"+ALLOC_SKIP, 9);
      settings.putRegister(countReg);
      allocCount(ALLOC_SKIP, countReg);

      countReg = new CountRegister("count"+ALLOC_MUSKIP, 9);
      settings.putRegister(countReg);
      allocCount(ALLOC_MUSKIP, countReg);

      countReg = new CountRegister("count"+ALLOC_BOX, 9);
      settings.putRegister(countReg);
      allocCount(ALLOC_BOX, countReg);

      countReg = new CountRegister("count"+ALLOC_TOKS, 9);
      settings.putRegister(countReg);
      allocCount(ALLOC_TOKS, countReg);

      countReg = new CountRegister("count"+ALLOC_INPUT, -1);
      settings.putRegister(countReg);
      allocCount(ALLOC_INPUT, countReg);

      countReg = new CountRegister("count"+ALLOC_OUTPUT, -1);
      settings.putRegister(countReg);
      allocCount(ALLOC_OUTPUT, countReg);

      countReg = new CountRegister("count"+ALLOC_MATHFAM, 3);
      settings.putRegister(countReg);
      allocCount(ALLOC_MATHFAM, countReg);

      countReg = new CountRegister("count"+ALLOC_LANGUAGE, 0);
      settings.putRegister(countReg);
      allocCount(ALLOC_LANGUAGE, countReg);

      countReg = new CountRegister("insc@unt", 255);
      settings.putRegister(countReg);
      allocCount(INS_COUNT, countReg);

      countReg = new CountRegister("allocationnumber");
      settings.putRegister(countReg);
      allocCount(ALLOC_NUMBER, countReg);

      countReg = new CountRegister("m@ne", -1);
      settings.putRegister(countReg);
      allocCount(MINUS_ONE, countReg);

   }

   private void initDefCatCodes()
   {
      catcodes = new CatCodeList[16];

      for (int i = 0; i < catcodes.length; i++)
      {
          if (i == TYPE_OTHER)
          {
             catcodes[i] = null;
             continue;
          }

          catcodes[i] = new CatCodeList();
      }

      catcodes[TYPE_ESC].add('\\');
      catcodes[TYPE_BG].add('{');
      catcodes[TYPE_EG].add('}');
      catcodes[TYPE_MATH].add('$');
      catcodes[TYPE_TAB].add('&');
      catcodes[TYPE_EOL].add('\n');
      catcodes[TYPE_EOL].add('\r');
      catcodes[TYPE_PARAM].add('#');
      catcodes[TYPE_SP].add('^');
      catcodes[TYPE_SB].add('_');

      catcodes[TYPE_SPACE].add(' ');
      catcodes[TYPE_SPACE].add('\t');

      for (int i = 'A'; i <= 'Z'; i++)
      {
         catcodes[TYPE_LETTER].add(Integer.valueOf(i));
      }

      for (int i = 'a'; i <= 'z'; i++)
      {
         catcodes[TYPE_LETTER].add(Integer.valueOf(i));
      }

      catcodes[TYPE_ACTIVE].add('~');
      catcodes[TYPE_COMMENT].add('%');

   }

   public boolean isDebugMode(int mode)
   {
      return (debugMode & mode) == mode;
   }

   public void setDebugMode(int mode)
   {
      debugMode = mode;
   }

   public void setDebugMode(int mode, PrintWriter writer)
   {
      setDebugMode(mode, writer, writer != null);
   }

   public void setDebugMode(int mode, PrintWriter writer, boolean doLogging)
   {
      setDebugMode(mode);
      logWriter = writer;
      logging = doLogging;
   }

   @Deprecated
   public int getDebugLevel()
   {
      return debugMode;
   }

   @Deprecated
   public void setDebugLevel(int level)
   {
      setDebugMode(level);
   }

   @Deprecated
   public void setDebugLevel(int level, PrintWriter writer)
   {
      setDebugMode(level, writer);
   }

   public void debugMessage(int mode, String msg)
   {
      if (isDebugMode(mode))
      {
         logMessage(msg);
      }
   }

   public void logMessage(String msg)
   {
      if (!logging) return;

      File file = getCurrentFile();

      String tag = "";

      if (file != null)
      {
         tag = file.getName();

         int lineNum = getLineNumber();

         if (lineNum > 0)
         {
            tag += ":"+lineNum;
         }

         tag += ": ";
      }

      if (logWriter == null)
      {
         System.out.println(tag+msg);
      }
      else
      {
         logWriter.println(tag+msg);
      }
   }

   public void logMessage(Throwable e)
   {
      if (logging)
      {
         if (logWriter == null)
         {
            e.printStackTrace();
         }
         else
         {
            if (e instanceof TeXSyntaxException)
            {
               logWriter.println(((TeXSyntaxException)e).getMessage(getListener().getTeXApp()));
            }

            e.printStackTrace(logWriter);
         }
      }
   }

   public void setLogWriter(PrintWriter writer)
   {
      logWriter = writer;
   }

   public void setLogging(boolean doLogging)
   {
      logging = doLogging;
   }

   public void warning(String msg)
   {
      getListener().getTeXApp().warning(this, msg);
      logMessage(msg);
   }

   public void warning(Throwable e)
   {
      if (e instanceof TeXSyntaxException)
      {
         getListener().getTeXApp().warning(this, 
          ((TeXSyntaxException)e).getMessage(getListener().getTeXApp()));
      }
      else
      {
         getListener().getTeXApp().warning(this, e.getMessage());
      }

      logMessage(e);
   }

   public void warningMessage(String msgTag, Object... params)
   {
      warning(getListener().getTeXApp().getMessage(msgTag, params));
   }

   public void error(Exception e)
   {
      getListener().getTeXApp().error(e);
      logMessage(e);
   }

   public void error(Exception e, int mode, String msg)
   {
      getListener().getTeXApp().error(e);

      if (isDebugMode(mode))
      {
         logMessage(msg);
      }

      logMessage(e);
   }

   public void message(String msgTag, Object... params)
   {
      TeXApp texapp = getListener().getTeXApp();
      String msg = texapp.getMessage(msgTag, params);
      texapp.message(msg);
      logMessage(msg);
   }

   public void message(TeXSyntaxException e)
   {
      TeXApp texapp = getListener().getTeXApp();
      String msg = e.getMessage(texapp);
      texapp.message(msg);
      logMessage(msg);
   }

   public void message(TeXObject obj)
   {
      String msg = obj.toString(this);
      getListener().getTeXApp().message(msg);
      logMessage(msg);
   }

   public boolean isActive(int c)
   {
      // check if in the active map

      ActiveChar ac = getActiveChar(Integer.valueOf(c));

      return ac != null;
   }

   // checks if c has cat code of given type
   public boolean isCatCode(int type, int codePoint)
   {
      if (type == TYPE_ACTIVE)
      {
         if (isActive(codePoint)) return true;
      }

      int catCode = getCatCode(codePoint);

      return type == catCode;
   }

   public void setCatCode(boolean isLocal, int c, int catCode)
   {
      if (isLocal)
      {
         if (isDebugMode(DEBUG_CATCODE))
         {
            logMessage(String.format("CatCode (local) %s -> %d", 
             new String(Character.toChars(c)), catCode));
         }

         settings.setCatCode(c, catCode);
      }
      else
      {
         setCatCode(c, catCode);
      }
   }

   public void setCatCode(int c, int catCode)
   {
      if (isDebugMode(DEBUG_CATCODE))
      {
         logMessage(String.format("CatCode (global) %s -> %d", 
          new String(Character.toChars(c)), catCode));
      }

      Integer character = Integer.valueOf(c);

      // remove it from its current catcode list

      for (int i = 0; i < catcodes.length; i++)
      {
         if (catcodes[i] == null) continue;

         if (catcodes[i].remove(character))
         {
            break;
         }
      }

      // "Other" cat code list is null
      if (catcodes[catCode] != null)
      {
         // add it to its new catcode list
         catcodes[catCode].add(character);
      }
   }

   // gets the cat code of c
   public int getRootCatCode(int c)
   {
      Integer character = Integer.valueOf(c);

      for (int i = 0; i < catcodes.length; i++)
      {
         if (catcodes[i] != null && catcodes[i].contains(character))
         {
            return i;
         }
      }

      return TYPE_OTHER;
   }

   public int getCatCode(int c)
   {
      return settings.getCatCode(c);
   }

   public int getLineNumber()
   {
      return reader == null ? -1 : reader.getLineNumber()+1;
   }

   public boolean isStack(Object obj)
   {
      return (obj != null && obj instanceof TeXObjectList
        && ((TeXObjectList)obj).isStack());
   }

   public boolean isUndefined(Object obj)
   {
      return (obj == null || obj instanceof Undefined);
   }

   public boolean isLetter(int c)
   {
      if (isCatCode(TYPE_LETTER, c)
           || (Character.isAlphabetic(c) && isCatCode(TYPE_OTHER, c)))
      {
         return true;
      }

      return false;
   }

   private int read() throws IOException
   {
      int cp = reader.read();

      if (isDebugMode(DEBUG_READ))
      {
         if (cp == -1)
         {
            logMessage("READ <EOF>");
         }
         else
         {
            logMessage(String.format("READ %s (0x%x)", 
               new String(Character.toChars(cp)), cp));
         }
      }

      return cp;
   }

   private void mark(int limit) throws IOException
   {
      reader.mark(limit);
   }

   private void reset() throws IOException
   {
      debugMessage(DEBUG_READ, "READER RESET");

      reader.reset();
   }

   public void terminate()
   {
      debugMessage(DEBUG_IO, "TERMINATE closing all open readers");

      reader.closeAll(listener.getTeXApp());
   }

   public void scan(String text, TeXObjectList list)
     throws IOException
   {
      TeXReader strReader = new TeXReader(reader, text);
      reader = strReader;

      while (reader == strReader)
      {
         if (!fetchNext(list))
         {
            break;
         }
      }
   }

   public TeXObjectList readLine(TeXReader otherReader, boolean retainEol)
     throws IOException
   {
      if (otherReader.isEnded())
      {
         return null;
      }

      TeXReader orgReader = reader;
      TeXReader orgParent = otherReader.getParent();

      TeXObjectList list = new TeXObjectList();

      TeXObjectList pending = null;

      try
      {
         if (size() > 0)
         {
            pending = getListener().createStack();
            pending.addAll(this);
            clear();
         }

         otherReader.setParent(reader);
         reader = otherReader;

         if (isDebugMode(DEBUG_IO))
         {
            logMessage("READLINE switch to "+otherReader+" PENDING: "+pending);
         }

         TeXObject obj = pop();

         while (!(obj instanceof Eol) && obj != null 
                  && !(otherReader.isEnded()))
         {
            list.add(obj);
            obj = pop();
         }

         if (retainEol && obj instanceof Eol)
         {
            list.add(obj);
         }

         if (list.size() > 0 && list.lastElement() instanceof Par)
         {
            list.remove(list.size()-1);
         }
      }
      catch (EOFException e)
      {
         if (list.size() == 0)
         {
            return null;
         }
      }
      finally
      {
         reader = orgReader;
         otherReader.setParent(orgParent);

         if (isDebugMode(DEBUG_IO))
         {
            logMessage("READLINE switching back to "+reader+" PENDING: "+pending);
         }

         if (pending != null)
         {
            addAll(0, pending);
            pending.clear();
         }
      }

      return list;
   }

   public TeXObjectList string(TeXParser parser)
    throws IOException
   {
      return string();
   }

   public TeXObjectList string()
    throws IOException
   {
      String text;

      if (size() == 0)
      {
         int c = read();

         while (isCatCode(TYPE_SPACE, c))
         {
            c = read();
         }

         if (isCatCode(TYPE_EOL, c))
         {
            parseEOL(c, this);
            text = pop().toString(this);
         }
         else if (isCatCode(TYPE_ESC, c))
         {
            readControlSequence(this, true);
            text = pop().toString(this);
         }
         else
         {
            text = ""+c;
         }
      }
      else
      {
         return pop().string(this);
      }

      return string(text);
   }

   public TeXObjectList string(String text)
   {
      TeXObjectList list = new TeXObjectList();

      for (int i = 0; i < text.length(); )
      {
         int codePoint = text.codePointAt(i);
         i += Character.charCount(codePoint);

         list.add(isLetter(codePoint) ?
            listener.getLetter(codePoint) : listener.getOther(codePoint));
      }

      return list;
   }

   /**
    * Identifies the given control sequence name as a verbatim
    * command.
    * @param csname the control sequence name (such as "verb")
    */ 
   public void addVerbCommand(String csname)
   {
      verbatim.add(csname);
   }

   /**
    * Tests whether or not the given control sequence name has been
    * identified as a verbatim command.
    * @param csname the control sequence name (such as "verb")
    * @return true if the control sequence name has been identified
    * as a verbatim command
    */ 
   public boolean isVerbCommand(String csname)
   {
      return verbatim.contains(csname);
   }

   private boolean parseEOL(int c, TeXObjectList list)
     throws IOException
   {
      return parseEOL(c, list, false);
   }

   private boolean parseEOL(int c, TeXObjectList list,
     boolean followsControlWord)
     throws IOException
   {
      boolean isNotEof = true;

      if (c == '\n')
      {
         // Is this just LF or is it LF+CR?
         // Or is it \n\n (paragraph break?)

         mark(1);

         c = read();

         if (c == '\n')
         {
            // Paragraph break
            // skip any further new lines

            isNotEof = skipNextEols(list);

            parFound(list);
         }
         else if (c == '\r')
         {
            // LF+CR

            // Is this followed by another eol character?

            mark(1);

            c = read();

            if (isCatCode(TYPE_EOL, c))
            {
               // we have a paragraph break. Skip any
               // following eol

               isNotEof = skipNextEols(list);

               parFound(list);
            }
            else
            {
               // not a paragraph break

               reset();

               eolFound(list, followsControlWord);
            }
         }
         else if (isCatCode(TYPE_EOL, c))
         {
            // user assigned EOL cat code to c
            // so we have a paragraph break

            // discard any following EOLs

            isNotEof = skipNextEols(list);

            parFound(list);
         }
         else
         {
            // not a paragraph break, just one LF

            try
            {
               reset();
            }
            catch (IOException e)
            {
               throw new EOFException();
            }

            eolFound(list, followsControlWord);
         }
      }
      else if (c == '\r')
      {
         // Is this just CR or is it CR+LF?
         // Or is it \r\r (paragraph break?)

         mark(1);

         c = read();

         if (c == '\r')
         {
            // Paragraph break
            // skip any further new lines

            isNotEof = skipNextEols(list);

            parFound(list);
         }
         else if (c == '\n')
         {
            // CR+LF

            // Is this followed by another eol character?

            mark(1);

            c = read();

            if (isCatCode(TYPE_EOL, c))
            {
               // we have a paragraph break. Skip any
               // following eol

               isNotEof = skipNextEols(list);

               parFound(list);
            }
            else
            {
               // not a paragraph break

               reset();

               eolFound(list, followsControlWord);
            }
         }
         else if (isCatCode(TYPE_EOL, c))
         {
            // user assigned EOL cat code to c
            // so we have a paragraph break

            // discard any following EOLs

            isNotEof = skipNextEols(list);

            parFound(list);
         }
         else
         {
            // not a paragraph break, just one LF
            reset();

            eolFound(list, followsControlWord);
         }
      }
      else // Neither CR nor LF
      {
         // User has assigned another character the EOL
         // category code

         mark(1);

         c = read();

         // Do we have a paragraph break?

         if (isCatCode(TYPE_EOL, c))
         {
            // paragraph break

            // skip any following EOL

            isNotEof = skipNextEols(list);

            parFound(list);
         }
         else
         {
            // not a paragraph break

            reset();

            eolFound(list, followsControlWord);
         }
      }

      if (!isNotEof)
      {
         return isNotEof;
      }

      // Skip any spaces at the start of the next line

      return skipNextSpaces(list);
   }

   private boolean skipNextEols(TeXObjectList list)
     throws IOException
   {
      int c = -1;

      mark(1);

      SkippedEols skipped = null;

      while ((c = read()) != -1)
      {
         if (!isCatCode(TYPE_EOL, c))
         {
            reset();
            break;
         }

         if (skipped == null)
         {
            skipped = listener.createSkippedEols();
         }

         Eol eol = listener.getEol();
         eol.setEol(new String(Character.toChars(c)));
         skipped.add(eol);

         mark(1);
      }

      if (skipped != null)
      {
         list.add(skipped);
      }

      return c != -1;
   }

   private boolean skipNextSpaces(TeXObjectList list)
     throws IOException
   {
      int c = -1;

      mark(1);

      SkippedSpaces skipped = null;

      boolean inVerb = settings.inVerb();

      while ((c = read()) != -1)
      {
         if (!isCatCode(TYPE_SPACE, c))
         {
            reset();
            break;
         }

         Space space = listener.getSpace();
         space.setSpace(c);

         if (inVerb)
         {// assume pseudo-verbatim

            list.add(space);
         }
         else
         {
            if (skipped == null)
            {
               skipped = listener.createSkippedSpaces();
            }

            skipped.add(space);
         }

         mark(1);
      }

      if (c == -1)
      {
         reset();
      }

      if (skipped != null)
      {
         list.add(skipped);
      }

      return c != -1;
   }

   private void eolFound(TeXObjectList list, boolean followsControlWord)
     throws IOException
   {
      if (followsControlWord)
      {
         SkippedEols skipped = listener.createSkippedEols();
         skipped.add(listener.getEol());

         list.add(skipped);
      }
      else
      {
         list.add(listener.getEol());
      }
   }

   private void parFound(TeXObjectList list)
     throws IOException
   {
      list.add(listener.getPar());
   }

   public static boolean isPar(TeXObject obj)
   {
      return obj != null && obj.isPar();
   }

   private boolean readComment(TeXObjectList list)
     throws IOException
   {
      Comment comment = listener.createComment();

      int c = -1;

      while ((c = read()) != -1)
      {
         if (isCatCode(TYPE_EOL, c))
         {
            if (c == '\n')
            {
               mark(1);
               c = read();

               if (c == '\r')
               {
                  // LF+CR
                  mark(1);
                  c = read();

                  if (isCatCode(TYPE_EOL, c))
                  {
                     list.add(comment);
                     list.add(listener.getPar());

                     return skipNextEols(list);
                  }
                  else
                  {
                     list.add(comment);
                  }
               }
               else if (c == '\n')
               {
                  // LF LF

                  list.add(comment);
                  list.add(listener.getPar());

                  return skipNextEols(list);
               }
               else if (isCatCode(TYPE_EOL, c))
               {
                  // LF EOL

                  list.add(comment);
                  list.add(listener.getPar());

                  return skipNextEols(list);
               }
               else if (c == -1)
               {
                  list.add(comment);
                  return false;
               }
               else
               {
                  // LF

                  list.add(comment);
                  reset();
                  return skipNextSpaces(list);
               }
            }
            else if (c == '\r')
            {
               mark(1);
               c = read();

               if (c == '\n')
               {
                  // CR+LF
                  mark(1);
                  c = read();

                  if (isCatCode(TYPE_EOL, c))
                  {
                     list.add(comment);
                     list.add(listener.getPar());

                     return skipNextEols(list);
                  }
                  else
                  {
                     list.add(comment);
                  }
               }
               else if (c == '\r')
               {
                  // CR + CR

                  list.add(comment);
                  list.add(listener.getPar());

                  return skipNextEols(list);
               }
               else if (isCatCode(TYPE_EOL, c))
               {
                  // CR EOL

                  list.add(comment);
                  list.add(listener.getPar());

                  return skipNextEols(list);
               }
               else if (c == -1)
               {
                  list.add(comment);
                  return false;
               }
               else
               {
                  // CR

                  list.add(comment);
                  reset();
                  return skipNextSpaces(list);
               }
            }
            else
            {
               // EOL

               mark(1);
               c = read();

               if (isCatCode(TYPE_EOL, c))
               {
                  // EOL EOL

                  list.add(comment);
                  list.add(listener.getPar());

                  return skipNextEols(list);
               }
               else if (c == -1)
               {
                  list.add(comment);
                  return false;
               }
               else
               {
                  // EOL

                  list.add(comment);
                  reset();
                  return skipNextSpaces(list);
               }
            }

            return true;
         }
         else
         {
            comment.appendCodePoint(c);
         }
      }

      list.add(comment);

      return c != -1;
   }

   private boolean readParam(TeXObjectList list, DoubleParam paramToken)
     throws IOException
   {
      mark(1);
      int c = read();

      if (c == -1)
      {
         paramToken.tail().setDigit(0);
         return false;
      }

      if (isCatCode(TYPE_PARAM, c))
      {
         DoubleParam dblParam = listener.getDoubleParam(paramToken);
         dblParam.setCharCode(c);

         readParam(list, dblParam);
      }
      else
      {
         if (c > '0' && c <= '9')
         {
            paramToken.tail().setDigit(c-'0');
            list.add(paramToken);
         }
         else if (isCatCode(TYPE_BG, c))
         {
            paramToken.tail().setDigit(-1);
            list.add(paramToken);
            reset();
         }
         else
         {
            paramToken.tail().setDigit(0);
            list.add(paramToken);
            reset();
         }
      }

      return true;
   }

   private boolean readParam(TeXObjectList list, int charCode)
     throws IOException
   {
      mark(1);
      int c = read();

      Param param = listener.getParam(0);
      param.setCharCode(charCode);

      if (c == -1)
      {
         list.add(param);
         return false;
      }

      if (isCatCode(TYPE_PARAM, c))
      {
         DoubleParam dblParam = listener.getDoubleParam(param);
         dblParam.setCharCode(c);

         return readParam(list, dblParam);
      }
      else if (c > '0' && c <= '9')
      {
         param.setDigit(c-'0');
         list.add(param);
      }
      else if (isCatCode(TYPE_BG, c))
      {
         param.setDigit(-1);
         list.add(param);
         reset();
      }
      else
      {
         list.add(new SpecialToken(param, charCode, TYPE_PARAM));
         reset();
      }

      return true;
   }

   public CatCodeChanger isCatCodeChanger(TeXObject obj)
   {
      obj = TeXParserUtils.resolve(obj, this);

      if (obj instanceof CatCodeChanger)
      {
         return (CatCodeChanger)obj;
      }

      return null;
   }

   public EgChar isEndGroup(TeXObject obj)
   {
      obj = TeXParserUtils.resolve(obj, this);

      if (obj instanceof EgChar)
      {
         return (EgChar)obj;
      }

      return null;
   }

   public BgChar isBeginGroup(TeXObject obj)
   {
      obj = TeXParserUtils.resolve(obj, this);

      if (obj instanceof BgChar)
      {
         return (BgChar)obj;
      }

      return null;
   }

   public boolean popRemainingGroup(TeXParser parser, Group group, 
      byte popStyle, BgChar bgChar)
      throws IOException
   {
      return popRemainingGroup(group, popStyle, bgChar);
   }

   public boolean popRemainingGroup(Group group, byte popStyle, BgChar bgChar)
      throws IOException
   {
      startGroup();

      try
      {
         while (true)
         {
            if (isEmpty())
            {
               if (!fetchNext(isShort(popStyle)))
               {
                  return false;
               }
            }

            TeXObject obj = remove(0);

            CatCodeChanger catCodeChanger = isCatCodeChanger(obj);

            if (catCodeChanger != null)
            {
               catCodeChanger.applyCatCodeChange(this);
            }

            EgChar egChar = isEndGroup(obj);

            if (egChar != null)
            {
               if (!egChar.matches(bgChar))
               {
                  throw new TeXSyntaxException(this,
                    TeXSyntaxException.ERROR_EXTRA_OR_FORGOTTEN,
                    egChar.toString(this), bgChar.toString(this));
               }

               return true;
            }

            bgChar = isBeginGroup(obj);

            if (isShort(popStyle) && obj.isPar())
            {
               throw new TeXSyntaxException(this,
                  TeXSyntaxException.ERROR_PAR_BEFORE_EG);
            }
            else if (bgChar != null)
            {
               Group subGrp = bgChar.createGroup(this);

               if (!popRemainingGroup(subGrp, popStyle, bgChar))
               {
                  group.add(subGrp);

                  return false;
               }

               group.add(subGrp);
            }
            else
            {
               group.add(obj);
            }
         }
      }
      finally
      {
         endGroup();
      }
   }

   private boolean readGroup(Group group, boolean isShort)
     throws IOException
   {
      int c;

      mark(1);

      startGroup();

      try
      {
         while ((c = read()) != -1)
         {
            if (isCatCode(TYPE_EG, c))
            {
               return true;
            }

            if (isCatCode(TYPE_BG, c))
            {
               Group subGroup = listener.createGroup();

               boolean result = readGroup(subGroup, isShort);
               group.add(subGroup);

               if (!result)
               {
                  return false;
               }

               continue;
            }

            reset();

            fetchNext(group, isShort);

            TeXObject obj = group.lastElement();

            CatCodeChanger catCodeChanger = isCatCodeChanger(obj);

            if (catCodeChanger != null)
            {
               catCodeChanger.applyCatCodeChange(this);
            }

            EgChar egChar = isEndGroup(obj);

            if (egChar != null)
            {
               group.remove(group.size()-1);
            }

            if (isShort && isPar(obj))
            {
               throw new TeXSyntaxException(this,
                  TeXSyntaxException.ERROR_PAR_BEFORE_EG);
            }

            mark(1);
         }

         if (c == -1)
         {
            throw new TeXSyntaxException(this,
               TeXSyntaxException.ERROR_NO_EG);
         }
      }
      finally
      {
         endGroup();
      }

      return c != -1;
   }

   private void readMath(MathGroup math)
     throws IOException
   {
      mark(1);
      int c = read();

      if (c == -1)
      {
         throw new TeXSyntaxException(this,
            TeXSyntaxException.ERROR_MISSING_ENDMATH);
      }
      else if (isCatCode(TYPE_MATH, c))
      {
         math.setInLine(false);
         readDisplayMath(math);
      }
      else
      {
         reset();
         math.setInLine(true);
         readInLineMath(math);
      }
   }

   private void readInLineMath(MathGroup math)
     throws IOException
   {
      mark(1);
      int c;

      startGroup();

      try
      {
         while ((c = read()) != -1)
         {
            if (isCatCode(TYPE_MATH, c))
            {
               return;
            }

            reset();

            fetchNext(math, true);

            TeXObject obj = math.lastElement();

            CatCodeChanger catCodeChanger = isCatCodeChanger(obj);

            if (catCodeChanger != null)
            {
               catCodeChanger.applyCatCodeChange(this);
            }

            mark(1);
         }
      }
      finally
      {
         endGroup();
      }

      throw new EOFException();
   }

   private void readDisplayMath(MathGroup math)
     throws IOException
   {
      mark(1);
      int c;

      startGroup();

      try
      {
         while ((c = read()) != -1)
         {
            if (isCatCode(TYPE_MATH, c))
            {
               mark(1);
               c = read();

               if (c == -1)
               {
                  throw new TeXSyntaxException(this,
                     TeXSyntaxException.ERROR_MISSING_ENDMATH);
               }

               if (!isCatCode(TYPE_MATH, c))
               {
                  reset();
                  throw new TeXSyntaxException(this,
                     TeXSyntaxException.ERROR_DOLLAR2_ENDED_WITH_DOLLAR);
               }

               return;
            }

            reset();

            fetchNext(math, true);

            TeXObject obj = math.lastElement();

            CatCodeChanger catCodeChanger = isCatCodeChanger(obj);

            if (catCodeChanger != null)
            {
               catCodeChanger.applyCatCodeChange(this);
            }

            mark(1);
         }
      }
      finally
      {
         endGroup();
      }

      throw new EOFException();
   }

   public void readTo(String terminator, TeXObjectList list)
     throws IOException
   {
      int n = terminator.length();
      mark(n);

      readTo(terminator, list, n, 0);
   }

   private void readTo(String terminator, TeXObjectList list,
     int n, int idx)
     throws IOException
   {
      int c = read();

      if (c == -1)
      {
         throw new TeXSyntaxException(this,
            TeXSyntaxException.ERROR_NOT_FOUND, terminator);
      }

      if (c == terminator.charAt(idx))
      {
         idx++;

         if (idx == n)
         {
            // finished
            return;
         }

         readTo(terminator, list, n, idx);
      }
      else
      {
         reset();

         if (!fetchNext(list))
         {
            throw new TeXSyntaxException(this,
               TeXSyntaxException.ERROR_NOT_FOUND, terminator);
         }

         mark(n);

         readTo(terminator, list, n, 0);
      }
   }

   public TeXObjectList popRemainingVerb(int delim)
     throws IOException
   {
      TeXObjectList charList = new TeXObjectList();

      while (size() > 0)
      {
         TeXObject obj = remove(0);

         String str = obj.toString(this);

         for (int i = 0, n = str.length(); i < n; )
         {
            int c = str.codePointAt(i);
            i += Character.charCount(c);

            if (c == delim)
            {
               addAll(0, listener.createString(str.substring(i)));
               return charList;
            }

            charList.add(listener.getOther(c));
         }
      }

      if (readVerb(delim, charList) == -1)
      {
         throw new EOFException();
      }

      return charList;
   }

   private int readVerb(int delim, TeXObjectList charList)
    throws IOException
   {
      int c;

      while ((c = read()) != -1)
      {
         if (c == delim)
         {
            return c;
         }

         charList.add(listener.getOther(c));
      }

      return -1;
   }

   private boolean readControlSequence(TeXObjectList list)
     throws IOException
   {
      return readControlSequence(list, false);
   }

   private boolean readControlSequence(TeXObjectList list, boolean doingString)
     throws IOException
   {
      StringBuilder macro = new StringBuilder();

      int c = -1;
      mark(1);

      while ((c = read()) != -1)
      {
         if (!isLetter(c))
         {
            TeXCsRef cs;

            if (isCatCode(TYPE_EOL, c))
            {
               // Control sequence ended with EOL.

               if (macro.length() == 0)
               {
                  // Treat as backslash space.
                  cs = new TeXCsRef(" ");
               }
               else
               {
                  cs = new TeXCsRef(macro.toString());
               }

               list.add(cs);

               parseEOL(c, list, true);
            }
            else if (macro.length() == 0)
            {
               // Control Symbol

               cs = new TeXCsRef(new String(Character.toChars(c)));

               list.add(cs);
            }
            else if (isCatCode(TYPE_SPACE, c))
            {
               // Control word ended by a space

               cs = new TeXCsRef(macro.toString());
               list.add(cs);
               reset();

               if (!skipNextSpaces(list))
               {
                  return false;
               }
            }
            else
            {
               // Control word ended by non-space

               reset();
               cs = new TeXCsRef(macro.toString());
               list.add(cs);
            }

            if (doingString) return true;

            if (isVerbCommand(cs.getName()))
            {
               c = read();

               if (c == '*')
               {
                  list.add(listener.getOther(c));
                  c = read();
               }

               int delim = c;

               TeXObjectList charList = new TeXObjectList();

               list.add(charList, false);

               charList.add(listener.getOther(c));

               c = readVerb(delim, charList);

               charList.add(listener.getOther(c));
            }
            else if (cs.getName().equals("detokenize"))
            {
               c = read();

               if (isCatCode(TYPE_BG, c))
               {
                  c = read();

                  while (!isCatCode(TYPE_EG, c) && c != -1)
                  {
                     list.add(listener.getOther(c));
                     c = read();
                  }
               }
               else
               {
                  list.add(listener.getOther(c));
               }
            }
            else if (cs.getName().equals("string"))
            {
               mark(1);
               c = read();

               while (isCatCode(TYPE_SPACE, c))
               {
                  mark(1);
                  c = read();
               }

               if (isCatCode(TYPE_ESC, c))
               {
                  reset();
               }
               else if (isCatCode(TYPE_LETTER, c))
               {
                  list.add(listener.getLetter(c));
               }
               else
               {
                  list.add(listener.getOther(c));
               }
            }

            return true;
         }

         mark(1);
         macro.appendCodePoint(c);    
      }

      if (c == -1)
      {
         list.add(new TeXCsRef(" "));
         return false;
      }

      return true;
   }

   public boolean fetchNext()
     throws IOException
   {
      return fetchNext(this, false);
   }

   public boolean fetchNext(boolean isShort)
     throws IOException
   {
      return fetchNext(this, isShort);
   }

   public boolean fetchNext(TeXObjectList list)
     throws IOException
   {
      return fetchNext(list, false);
   }

   public boolean fetchNext(TeXObjectList list, boolean isShort)
     throws IOException
   {
      if (reader == null)
      {
         logMessage("NULL reader");

         return false;
      }

      int c = read();

      if (c == -1) return false;

      if (isCatCode(TYPE_EOL, c))
      {
         parseEOL(c, list);
      }
      else if (isCatCode(TYPE_ESC, c))
      {
        return readControlSequence(list);
      }
      else if (isCatCode(TYPE_COMMENT, c))
      {
         return readComment(list);
      }
      else if (isCatCode(TYPE_PARAM, c))
      {
         return readParam(list, c);
      }
      else if (isCatCode(TYPE_ACTIVE, c))
      {
         TeXObject obj = listener.getActiveChar(c);

         if (obj == null)
         {
            throw new TeXSyntaxException(this, 
              TeXSyntaxException.ERROR_UNDEFINED_CHAR, 
              new String(Character.toChars(c)));
         }

         list.add(obj);
      }
      else if (isCatCode(TYPE_SP, c))
      {
         list.add(listener.createSpChar(c));
      }
      else if (isCatCode(TYPE_SB, c))
      {
         list.add(listener.createSbChar(c));
      }
      else if (isCatCode(TYPE_TAB, c))
      {
         list.add(listener.getTab(c));
      }
      else if (isCatCode(TYPE_MATH, c))
      {
         MathGroup math = listener.createMathGroup();
         list.add(math);
         readMath(math);
      }
      else if (isCatCode(TYPE_BG, c))
      {
         list.add(listener.getBgChar(c));
      }
      else if (isCatCode(TYPE_EG, c))
      {
         list.add(listener.getEgChar(c));
      }
      else if (isCatCode(TYPE_SPACE, c))
      {
         Space space = listener.getSpace();
         space.setSpace(c);
         list.add(space);
         return skipNextSpaces(list);
      }
      else if (isLetter(c))
      {
         list.add(listener.getLetter(c));
      }
      else
      {
         list.add(listener.getOther(c));
      }

      try
      {
         mark(1);
      }
      catch (IOException e)
      {
         // the stream may have closed by now

         return false;
      }

      return true;
   }

   public void parse(TeXReader reader)
     throws IOException
   {
      parse(reader, TeXMode.INHERIT);
   }

   @Deprecated
   public void parse(TeXReader reader, int mode)
     throws IOException
   {
      switch (mode)
      {
         case TeXSettings.INHERIT:
            parse(reader, TeXMode.INHERIT);
         break;
         case TeXSettings.MODE_TEXT:
            parse(reader, TeXMode.TEXT);
         break;
         case TeXSettings.MODE_INLINE_MATH:
            parse(reader, TeXMode.INLINE_MATH);
         break;
         case TeXSettings.MODE_DISPLAY_MATH:
            parse(reader, TeXMode.DISPLAY_MATH);
         break;
         default:
            throw new IllegalArgumentException("Invalid mode "+mode);
      }
   }

   public void parse(TeXReader reader, TeXMode mode)
     throws IOException
   {
      if (reader != this.reader)
      {
         // Is the current stack non-empty?
         // If it is, save remaining content for later

         boolean down = true;

         if (this.reader != null)
         {
            if (isDebugMode(DEBUG_IO))
            {
               logMessage("CURRENT READER "+this.reader);
            }

            if (this.reader == reader.getParent())
            {
               if (isDebugMode(DEBUG_IO))
               {
                  logMessage("MOVING DOWN TO NESTED FILE "+reader);
               }
            }
            else if (this.reader.getParent() == reader)
            {
               down = false;

               if (isDebugMode(DEBUG_IO))
               {
                  logMessage("MOVING UP TO PARENT FILE "+reader);
               }
            }
            else
            {
               if (isDebugMode(DEBUG_IO))
               {
                  logMessage("MOVING SIDEWAYS(??) TO NESTED FILE "+reader);
               }

               reader.setParent(this.reader);
            }
         }

         if (down && !isEmpty())
         {
            TeXObjectList pending = reader.getPending();

            if (pending == null)
            {
               pending = new TeXObjectList(size());
            }

            pending.addAll(this);
            clear();

            reader.setPending(pending);
            pending = null;
         }
         else if (!down)
         {
            TeXObjectList pending = reader.getPending();

            if (pending != null)
            {
               push(pending, true);
            }

            reader.setPending(null);
         }

         if (isDebugMode(DEBUG_IO))
         {
            logMessage("PARSE switching from "+this.reader + " to "+reader);
         }

         this.reader = reader;
      }

      if (isDebugMode(DEBUG_IO))
      {
         logMessage("PARSE setting mode: "+mode);
      }

      settings.setMode(mode);

      try
      {
         boolean done = false;

         while (!done)
         {
            boolean eof = (!fetchNext() || isEmpty());

            if (isDebugMode(DEBUG_IO))
            {
               if (eof)
               {
                  logMessage("PARSE FETCH NEXT EOF FOUND");
               }
               else
               {
                  logMessage("PARSE FETCH NEXT "+toString());
               }
            }

            if (eof)
            {
               closeReader(reader);

               reader = this.reader;

               if (this.reader == null)
               {
                  done = true;
               }
            }

            while (size() > 0)
            {
               TeXObject object = pop();

               if (isDebugMode(DEBUG_POPPED))
               {
                  logMessage("PARSE POPPED "+object);
               }

               try
               {
                  object.process(this);
               }
               catch (EOFException e)
               {
                  debugMessage(DEBUG_IO, "EOF while processing object "+object);

                  if (reader != null)
                  {
                     closeReader(reader);
                  }

                  reader = this.reader;

                  if (this.reader == null)
                  {
                     done = true;
                  }
               }
               catch (TeXSyntaxException e)
               {
                  error(e);
               }
            }
         }
      }
      catch (EOFException e)
      {
         debugMessage(DEBUG_IO, "EOF while fetching next from "+this.reader);

         closeReader(this.reader);
      }
   }

   private void closeReader(TeXReader reader)
    throws IOException
   {
      TeXObjectList pending = reader.getPending();

      reader.setPending(null);

      reader.close();

      this.reader = reader.getParent();

      while (this.reader != null)
      {
         if (isDebugMode(DEBUG_IO))
         {
            logMessage("CLOSE READER switching from child "+ reader
              + " to parent "+this.reader);
         }

         if (this.reader.isClosed())
         {
            if (this.reader.hasPending())
            {
               if (pending == null)
               {
                  pending = this.reader.getPending();
               }
               else
               {
                  pending.addAll(this.reader.getPending());
               }

               this.reader.setPending(null);
            }

            reader = this.reader;
            this.reader = this.reader.getParent();
         }
         else
         {
            break;
         }
      }

      if (pending != null)
      {
         debugMessage(DEBUG_IO, "CLOSE READER processing pending");

         pending.process(this);
      }
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      if (isDebugMode(DEBUG_PROCESSING))
      {
         logMessage("[Do nothing] PROCESS "+toString());
      }
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      if (isDebugMode(DEBUG_PROCESSING))
      {
         logMessage("PROCESS "+toString()+" SUBSTACK: "+stack);
      }

      if (stack != null && stack != this)
      {
         push(stack, true);
      }
   }

   public void processAction(TeXParserActionObject obj)
     throws IOException
   {
      switch (obj.getAction())
      {
         case INPUT_FILE:
           parse(obj.getFile(), currentInputCharset, obj.getPending());
         break;
         case MODE_CHANGE:
           settings.setMode((TeXMode)obj.getData());
         break;
      }
   }

   public void parse(File file)
     throws IOException
   {
      parse(file, getListener().getCharSet());
   }

   public void parse(File file, Charset charset)
     throws IOException
   {
      parse(file, charset, null);
   }

   public void parse(File file, Charset charset, TeXObjectList stack)
     throws IOException
   {
      if (baseDir == null)
      {
         setBaseDir(file.getParentFile());

         if (baseDir == null)
         {
            setBaseDir(new File("."));
         }
      }

      if (charset == null)
      {
         charset = getListener().getTeXApp().getDefaultCharset();
      }

      currentInputCharset = charset;

      if (!getListener().getTeXApp().isReadAccessAllowed(file))
      {
         warningMessage(TeXApp.MESSAGE_NO_READ, file);

         return;
      }

      if (jobname == null)
      {
         jobname = file.getName();

         int idx = jobname.lastIndexOf(".");

         if (idx > 0)
         {
            jobname = jobname.substring(0, idx);
         }
      }

      try
      {
         debugMessage(DEBUG_IO, "PARSE FILE: "+file);
         listener.beginParse(file, charset);

         TeXReader nextReader = new TeXReader(this.reader, file, charset);

         if (stack != null && stack != this && !stack.isEmpty())
         {
            nextReader.setPending(stack);
         }

         debugMessage(DEBUG_IO, "READER: "+nextReader);

         parse(nextReader);
      }
      catch (EOFException e)
      {
         if (isDebugMode(DEBUG_IO))
         {
            logMessage("EOF parsing file: "+file);
            logMessage("Current reader: "+reader);
         }
      }
      finally
      {
         listener.endParse(file);

         if (reader != null)
         {
            reader.close();
            reader = reader.getParent();
         }
      }
   }

   public void parse(TeXPath path)
     throws IOException
   {
      parse(path, getListener().getCharSet());
   }

   public void parse(TeXPath path, Charset charset)
     throws IOException
   {
      parse(path, charset, null);
   }

   public void parse(TeXPath path, Charset charset, TeXObjectList stack)
     throws IOException
   {
      if (baseDir == null)
      {
         setBaseDir(path.getBaseDir());

         if (baseDir == null)
         {
            setBaseDir(new File("."));
         }
      }

      if (charset == null)
      {
         charset = getListener().getTeXApp().getDefaultCharset();
      }

      if (!getListener().getTeXApp().isReadAccessAllowed(path))
      {
         warningMessage(TeXApp.MESSAGE_NO_READ, path);

         return;
      }

      File file = path.getFile();

      if (jobname == null)
      {
         jobname = file.getName();

         int idx = jobname.lastIndexOf(".");

         if (idx > 0)
         {
            jobname = jobname.substring(0, idx);
         }
      }

      try
      {
         listener.beginParse(file, charset);

         TeXReader nextReader = new TeXReader(this.reader, file, charset);

         if (stack != null && stack != this && !stack.isEmpty())
         {
            nextReader.setPending(stack);
         }

         parse(nextReader);
      }
      catch (EOFException e)
      {
      }
      finally
      {
         listener.endParse(file);

         if (reader != null)
         {
            reader.close();
            reader = reader.getParent();
         }
      }
   }

   public TeXObject pop()
     throws IOException
   {
      if (size() == 0)
      {
         fetchNext();

         if (size() == 0)
         {
            throw new EOFException();
         }
      }

      return remove(0);
   }

   public TeXObject popNextArg()
     throws IOException
   {
      return popNextArg(POP_IGNORE_LEADING_SPACE);
   }

   public TeXObject popNextArg(byte popStyle)
     throws IOException
   {
      boolean skipIgnoreables = !isRetainIgnoreables(popStyle);
      boolean skipLeadingWhiteSpace = isIgnoreLeadingSpace(popStyle);

      if (skipIgnoreables && skipLeadingWhiteSpace)
      {
         while (size() == 0)
         {
            if (!fetchNext(isShort(popStyle)))
            {
               throw new EOFException();
            }

            TeXObject obj = get(0);

            if (!((obj instanceof Ignoreable) || (obj instanceof WhiteSpace)))
            {
               break;
            }

            pop();
         }
      }
      else if (skipIgnoreables)
      {
         while (size() == 0)
         {
            if (!fetchNext(isShort(popStyle)))
            {
               throw new EOFException();
            }

            if (!(get(0) instanceof Ignoreable))
            {
               break;
            }

            pop();
         }
      }
      else if (skipLeadingWhiteSpace)
      {
         while (size() == 0)
         {
            if (!fetchNext(isShort(popStyle)))
            {
               throw new EOFException();
            }

            if (!(get(0) instanceof WhiteSpace))
            {
               break;
            }

            pop();
         }
      }
      else if (size() == 0)
      {
         if (!fetchNext(isShort(popStyle)))
         {
            throw new EOFException();
         }
      }

      return popArg(popStyle);
   }

   public TeXObject popNextArg(int openDelim, int closeDelim)
     throws IOException
   {
      return popNextArg(POP_IGNORE_LEADING_SPACE, openDelim, closeDelim);
   }

   public TeXObject popNextArg(byte popStyle, int openDelim, int closeDelim)
     throws IOException
   {
      if (size() == 0)
      {
         fetchNext(isShort(popStyle));
      }

      return popArg(popStyle, openDelim, closeDelim);
   }

   public Numerical popNumericalArg(int openDelim, int closeDelim)
     throws IOException
   {
      TeXObject obj = popNextArg(POP_SHORT, openDelim, closeDelim);

      if (obj == null) return null;

      if (obj instanceof Numerical)
      {
         return (Numerical)obj;
      }

      if (obj instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)obj).expandfully(this);

         if (expanded != null)
         {
            obj = expanded;
         }
      }

      if (obj instanceof TeXObjectList)
      {
         return ((TeXObjectList)obj).popNumerical(this);
      }

      return new UserNumber(this, obj.toString(this));
   }

   public Numerical popNumericalArg()
     throws IOException
   {
      TeXObject obj = popNextArg(POP_SHORT);

      if (obj == null) return null;

      if (obj instanceof Numerical)
      {
         return (Numerical)obj;
      }

      if (obj instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)obj).expandfully(this);

         if (expanded != null)
         {
            obj = expanded;
         }
      }

      if (obj instanceof TeXObjectList)
      {
         return ((TeXObjectList)obj).popNumerical(this);
      }

      return new UserNumber(this, obj.toString(this));
   }


   public TeXObject popArg()
    throws IOException
   {
      return super.popArg(this);
   }

   public TeXObject popArg(byte popStyle)
    throws IOException
   {
      return super.popArg(this, popStyle);
   }

   public TeXObject popArg(byte popStyle, int openDelim, int closeDelim)
   throws IOException
   {
      return super.popArg(this, popStyle, openDelim, closeDelim);
   }

   public TeXObject expandedPopStack(TeXParser parser) throws IOException
   {
      return parser.expandedPopStack();
   }

   public TeXObject expandedPopStack(TeXParser parser, byte popStyle)
      throws IOException
   {
      return parser.expandedPopStack(popStyle);
   }

   public TeXObject expandedPopStack() throws IOException
   {
      return expandedPopStack((byte)0);
   }

   public TeXObject expandedPopStack(byte popStyle) throws IOException
   {
      TeXObject object = popStack(popStyle);

      object = TeXParserUtils.resolve(object, this);

      BgChar bgChar = isBeginGroup(object);

      if (bgChar != null)
      {
         Group group = bgChar.createGroup(this);
         popRemainingGroup(group, popStyle, bgChar);

         return group;
      }

      if (!(object instanceof Expandable))
      {
         return object;
      }

      TeXObjectList expanded = ((Expandable)object).expandfully(this);

      if (expanded != null)
      {
         if (expanded.size() == 0)
         {
            return expanded;
         }

         object = expanded.remove(0);

         bgChar = isBeginGroup(object);

         if (bgChar != null)
         {
            Group grp = bgChar.createGroup(this);
            expanded.popRemainingGroup(this, grp, popStyle, bgChar);
            push(expanded, true);

            return grp;
         }

         push(expanded, true);
         return object;
      }

      return object;
   }

   public TeXObject popStack(TeXParser parser) throws IOException
   {
      return popStack();
   }

   public TeXObject popStack(TeXParser parser, byte popStyle)
      throws IOException
   {
      return popStack(popStyle);
   }

   public TeXObject popStack() throws IOException
   {
      return popStack((byte)0);
   }

   public TeXObject popStack(byte popStyle)
     throws IOException
   {
      TeXObject object = null;

      boolean skipIgnoreables = !isRetainIgnoreables(popStyle);
      boolean skipLeadingWhiteSpace = isIgnoreLeadingSpace(popStyle);

      boolean done = false;

      if (skipIgnoreables && skipLeadingWhiteSpace)
      {
         while (!done)
         {
            if (isEmpty())
            {
               fetchNext();
            }

            if (isEmpty())
            {
               throw new EOFException();
            }

            object = remove(0);

            if (!((object instanceof Ignoreable) || (object instanceof WhiteSpace)))
            {
               done = true;
            }
         }
      }
      else if (skipIgnoreables)
      {
         while (!done)
         {
            if (isEmpty())
            {
               fetchNext();
            }

            if (isEmpty())
            {
               throw new EOFException();
            }

            object = remove(0);

            if (!(object instanceof Ignoreable))
            {
               done = true;
            }
         }
      }
      else if (skipLeadingWhiteSpace)
      {
         while (!done)
         {
            if (isEmpty())
            {
               fetchNext();
            }

            if (isEmpty())
            {
               throw new EOFException();
            }

            object = remove(0);

            if (!(object instanceof WhiteSpace))
            {
               done = true;
            }
         }
      }
      else
      {
         if (isEmpty())
         {
            fetchNext(isShort(popStyle));
         }

         if (!fetchNext())
         {
            throw new EOFException();
         }

         object = remove(0);
      }

      BgChar bgChar = isBeginGroup(object);

      if (bgChar != null)
      {
         Group group = bgChar.createGroup(this);
         popRemainingGroup(group, popStyle, bgChar);
         return group;
      }

      return object;
   }

   public TeXObject popToken()
     throws IOException
   {
      return popToken((byte)0);
   }

   public TeXObject popToken(byte popStyle)
     throws IOException
   {
      if (size() == 0)
      {
         fetchNext(isShort(popStyle));
      }

      if (size() == 0)
      {
         throw new EOFException();
      }

      TeXObject object = remove(0);

      if (object instanceof WhiteSpace && isIgnoreLeadingSpace(popStyle))
      {
         return popToken(popStyle);
      }
      else if (object instanceof Ignoreable && !isRetainIgnoreables(popStyle))
      {
         listener.skipping((Ignoreable)object);

         return popToken(popStyle);
      }

      return object;
   }

   public TeXObjectList popToGroup(byte popStyle)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      while (true)
      {
         if (size() == 0)
         {
            fetchNext(isShort(popStyle));
         }

         if (size() == 0)
         {
            throw new EOFException();
         }

         TeXObject obj = firstElement();

         BgChar bgChar = isBeginGroup(obj);

         if (obj instanceof Group || bgChar != null)
         {
            break;
         }

         obj = remove(0);

         if (obj instanceof Ignoreable && !isRetainIgnoreables(popStyle))
         {
            listener.skipping((Ignoreable)obj);
         }
         else
         {
            list.add(obj);
         }
      }

      return list;
   }

   // For commands that haven't been identified as verbatim.
   // For example, commands that have an optional argument.
   public TeXObjectList popVerb()
     throws IOException
   {
      int delim = -1;

      TeXObjectList charList = new TeXObjectList();

      // This will be problematic if there's anything already on the
      // parser's stack, but if there is, let's hope it's just
      // ignoreables, white space or something that can be
      // detokenized.

      while (size() > 0)
      {
         TeXObject obj = firstElement();

         if (obj instanceof Ignoreable || obj instanceof WhiteSpace)
         {
            remove(0);
         }
         else
         {
            break;
         }
      }

      if (isEmpty())
      {
         delim = read();

         readVerb(delim, charList);
      }
      else
      {
         String str = "";

         while (str.isEmpty() && size() > 0)
         {
            TeXObject obj = remove(0);

            str = obj.toString(this);
         }

         if (str.isEmpty())
         {
            delim = read();

            readVerb(delim, charList);
         }
         else
         {
            delim = str.codePointAt(0);
            int i = Character.charCount(delim);

            boolean found = false;

            while (!found)
            {
               for ( ; i < str.length() ; )
               {
                  int cp = str.codePointAt(i);
                  i += Character.charCount(cp);

                  if (cp == delim)
                  {
                     if (i < str.length())
                     {
                        addAll(0, listener.createString(str.substring(i)));
                     }

                     found = true;
                  }
                  else
                  {
                     charList.add(listener.getOther(cp)); 
                  }
               }

               if (!found)
               {
                  if (size() > 0)
                  {
                     TeXObject obj = remove(0);
                     str = obj.toString(this);
                  }
                  else
                  {
                     readVerb(delim, charList);
                     found = true;
                  }
               }

               i = 0;
            }

         }
      }

      return charList;
   }

   public TeXNumber popNumber()
     throws IOException
   {
      return popNumber(this);
   }

   public TeXDimension popDimension()
     throws IOException
   {
      return popDimension(this);
   }

   public Numerical popNumerical()
     throws IOException
   {
      return popNumerical(this);
   }

   public Register popRegister()
     throws IOException
   {
      return popRegister(this);
   }

   public TeXObject peekStack(byte popStyle)
     throws IOException
   {
      int idx = 0;

      if (size() == 0)
      {
         fetchNext();

         if (size() == 0)
         {
            return null;
         }
      }

      TeXObject obj = firstElement();

      if (isIgnoreLeadingSpace(popStyle))
      {
         while (obj instanceof Ignoreable 
             || obj instanceof WhiteSpace)
         {
            idx++;

            if (size() == idx)
            {
               fetchNext();
            }

            if (idx >= size())
            {
               return null;
            }

            obj = get(idx);
         }
      }
      else
      {
         while (obj instanceof Ignoreable)
         {
            idx++;

            if (size() == idx)
            {
               fetchNext();
            }

            if (idx >= size())
            {
               return null;
            }

            obj = get(idx);
         }
      }

      return obj;
   }

   public TeXObject peekStack(int index)
   {
      return size() <= index ? null : get(index);
   }

   public void setWriter(Writer writer)
   {
      this.writer = writer;
   }

   public Writer getWriter()
   {
      return writer;
   }

   public TeXReader getReader()
   {
      return reader;
   }

   public File getCurrentParentFile()
   {
      if (reader == null)
      {
         return null;
      }

      Object source = reader.getSource();

      if (source != null && (source instanceof File))
      {
         return ((File)source).getParentFile();
      }

      return null;
   }

   public void setBaseDir(File dir)
   {
      baseDir = dir;
   }

   public void setBaseDir(Path path)
   {
      baseDir = (path == null ? null : path.toFile());
   }

   public File getBaseDir()
   {
      return baseDir;
   }

   public void setReader(TeXReader reader)
   {
      this.reader = reader;
   }

   public TeXParserListener getListener()
   {
      return listener;
   }

   public File getCurrentFile()
   {
      if (reader == null)
      {
         return null;
      }

      Object source = reader.getSource();

      return source instanceof File ? (File)source : null;
   }

   public boolean isMathMode()
   {
      return TeXMode.isMath(settings.getMode());
   }

   public void putControlSequence(ControlSequence cs)
   {
      settings.undefControlSequence(cs.getName());
      settings.putControlSequence(cs);

      if (cs instanceof Declaration)
      {
         EndDeclaration endDec = new EndDeclaration(cs.getName());
         settings.putControlSequence(endDec);
      }

      csTable.put(cs.getName(), cs);

      if (isDebugMode(DEBUG_CS))
      {
         logMessage("Globally defining "+cs);
      }

      if (cs instanceof Declaration && isLetter(cs.getName().charAt(0)))
      {
         EndDeclaration endDec = new EndDeclaration(cs.getName());
         csTable.put(endDec.getName(), endDec);

         if (isDebugMode(DEBUG_CS))
         {
            logMessage("Globally defining "+endDec);
         }
      }
   }

   public void putControlSequence(boolean isLocal, ControlSequence cs)
   {
      if (isLocal)
      {
         settings.putControlSequence(cs);

         if (isDebugMode(DEBUG_CS))
         {
            logMessage("Locally defining "+cs);
         }

         if (cs instanceof Declaration)
         {
            EndDeclaration endDec = new EndDeclaration(cs.getName());
            settings.putControlSequence(endDec);

            if (isDebugMode(DEBUG_CS))
            {
               logMessage("Locally defining "+endDec);
            }
         }
      }
      else
      {
         putControlSequence(cs);
      }
   }

   public ControlSequence getControlSequence(String name)
   {
      ControlSequence cs = settings.getControlSequence(name);

      if (cs != null)
      {
         if (isDebugMode(DEBUG_CS))
         {
            logMessage("Fetched LOCAL control sequence: "+cs);
         }

         return cs;
      }

      cs = csTable.get(name);

      if (isDebugMode(DEBUG_CS))
      {
         if (cs == null)
         {
            logMessage("No control sequence found for: "+name);
         }
         else
         {
            logMessage("Fetched GLOBAL control sequence: "+cs);
         }
      }

      return cs;
   }

   public ControlSequence removeControlSequence(boolean isLocal, String name)
   {
      if (isLocal)
      {
         ControlSequence cs = settings.removeLocalControlSequence(name);

         if (cs != null)
         {
            return cs;
         }
      }

      return csTable.remove(name);
   }

   public ActiveChar removeActiveChar(int code)
   {
      return activeTable.remove(Integer.valueOf(code));
   }

   public ActiveChar removeActiveChar(boolean isLocal, int code)
   {
      if (isLocal)
      {
         return settings.removeActiveChar(code);
      }
      else
      {
         return removeActiveChar(code);
      }
   }

   public void putActiveChar(ActiveChar activeChar)
   {
      activeTable.put(Integer.valueOf(activeChar.getCharCode()),
        activeChar);
   }

   public void putActiveChar(boolean isLocal, ActiveChar activeChar)
   {
      if (isLocal)
      {
         settings.putActiveChar(activeChar);
      }
      else
      {
         putActiveChar(activeChar);
      }
   }

   public ActiveChar getActiveChar(int charCode)
   {
      Integer intCode = Integer.valueOf(charCode);

      ActiveChar activeChar = settings.getActiveChar(intCode);

      if (activeChar != null)
      {
         return activeChar;
      }

      return activeTable.get(intCode);
   }

   public void startGroup()
   {
      settings = new TeXSettings(settings, this);
   }

   public void endGroup()
    throws TeXSyntaxException
   {
      if (settings.getParent() == null)
      {
         throw new TeXSyntaxException(this,
            TeXSyntaxException.ERROR_UNEXPECTED_EG);
      }

      TeXObjectList afterGroup = settings.getAfterGroup();
      settings = settings.getParent();

      if (afterGroup != null)
      {
         addAll(0, afterGroup);
      }
   }

   public TeXSettings getSettings()
   {
      return settings;
   }

   public int getSpecialChar(int type, int def)
   {
      if (catcodes[type].size() == 0)
      {
         return def;
      }

      return catcodes[type].firstElement().intValue();
   }

   public int getEscChar()
   {
      return getSpecialChar(TYPE_ESC, '\\');
   }

   public int getMathChar()
   {
      return getSpecialChar(TYPE_MATH, '$');
   }

   public String getMathDelim(boolean isinline)
   {
      int c = getSpecialChar(TYPE_MATH, '$');

      String charStr = new String(Character.toChars(c));

      return isinline ? charStr : String.format("%s%s", charStr, charStr);
   }

   public int getCommentChar()
   {
      return getSpecialChar(TYPE_COMMENT, '%');
   }

   public int getBgChar()
   {
      return getSpecialChar(TYPE_BG, '{');
   }

   public int getEgChar()
   {
      return getSpecialChar(TYPE_EG, '}');
   }

   public int getParamChar()
   {
      return getSpecialChar(TYPE_PARAM, '#');
   }

   public int getSpChar()
   {
      return getSpecialChar(TYPE_SP, '^');
   }

   public int getSbChar()
   {
      return getSpecialChar(TYPE_SB, '_');
   }

   public int getTabChar()
   {
      return getSpecialChar(TYPE_TAB, '&');
   }

   public String getJobname()
   {
      return jobname;
   }

   public String toString()
   {
      StringBuilder builder = new StringBuilder();

      builder.append(getClass().getSimpleName()+"[reader="+reader+",buffered: ");

      for (TeXObject object : this)
      {
         builder.append(object.toString());
      }

      builder.append("]");

      return builder.toString();
   }

   public String toString(TeXParser parser)
   {
      StringBuilder builder = new StringBuilder();

      for (TeXObject object : this)
      {
         builder.append(object.toString(parser));
      }

      return builder.toString();
   }

   public TeXObject expandonce(TeXObject object, TeXObjectList stack)
    throws IOException
   {
      if (isDebugMode(DEBUG_EXPANSION_ONCE_LIST))
      {
         logMessage("Attempting to expand once: "+object);
      }

      if (isDebugMode(DEBUG_EXPANSION_ONCE))
      {
         logMessage("Attempting to expand once: "+object.toString(this));
      }

      if (object instanceof Expandable && object.canExpand())
      {
         TeXObjectList expanded;

         if (this == stack || stack == null)
         {
            expanded = ((Expandable)object).expandonce(this);
         }
         else
         {
            expanded = ((Expandable)object).expandonce(this, stack);
         }

         if (expanded != null)
         {
            object = expanded;

            if (isDebugMode(DEBUG_EXPANSION_ONCE_LIST))
            {
               logMessage("Expanded: "+object);
            }

            if (isDebugMode(DEBUG_EXPANSION_ONCE))
            {
               logMessage("Expanded: "+object.toString(this));
            }
         }
         else if (isDebugMode(DEBUG_EXPANSION_ONCE_LIST)
               || isDebugMode(DEBUG_EXPANSION_ONCE))
         {
            logMessage("Expansion failed");
         }
      }
      else if (isDebugMode(DEBUG_EXPANSION_ONCE_LIST)
            || isDebugMode(DEBUG_EXPANSION_ONCE))
      {
         logMessage("Can't expand");
      }

      return object;
   }

   public TeXObject expandfully(TeXObject object, TeXObjectList stack)
    throws IOException
   {
      if (isDebugMode(DEBUG_EXPANSION_LIST))
      {
         logMessage("Attempting to fully expand: "+object);
      }

      if (isDebugMode(DEBUG_EXPANSION))
      {
         logMessage("Attempting to fully expand: "+object.toString(this));
      }

      if (object instanceof Expandable && object.canExpand())
      {
         TeXObjectList expanded;

         if (this == stack || stack == null)
         {
            expanded = ((Expandable)object).expandfully(this);
         }
         else
         {
            expanded = ((Expandable)object).expandfully(this, stack);
         }

         if (expanded != null)
         {
            object = expanded;

            if (isDebugMode(DEBUG_EXPANSION_LIST))
            {
               logMessage("Expanded: "+object);
            }

            if (isDebugMode(DEBUG_EXPANSION))
            {
               logMessage("Expanded: "+object.toString(this));
            }
         }
         else if (isDebugMode(DEBUG_EXPANSION_LIST) || isDebugMode(DEBUG_EXPANSION))
         {
            logMessage("Expansion failed");
         }
      }
      else if (isDebugMode(DEBUG_EXPANSION_LIST) || isDebugMode(DEBUG_EXPANSION))
      {
         logMessage("Can't expand");
      }

      return object;
   }

   /**
    * Expands the given object. Note that this will clear the object
    * if the object is a list. If the object needs to be retained,
    * pass a copy.
    */ 
   public String expandToString(TeXObject object, TeXObjectList stack)
    throws IOException
   {
      if (isDebugMode(DEBUG_EXPANSION_LIST))
      {
         logMessage("Expanding to string: "+object);
      }

      if (isDebugMode(DEBUG_EXPANSION))
      {
         logMessage("Expanding to string: "+object.toString(this));
      }

      String result = "";

      if (!object.isEmpty())
      {
         if (object instanceof TextualContentCommand)
         {
            result = ((TextualContentCommand)object).getText();
         }
         else
         {
            result = expandfully(object, stack).toString(this);
         }
      }

      if (isDebugMode(DEBUG_EXPANSION) || isDebugMode(DEBUG_EXPANSION_LIST))
      {
         logMessage("String: "+result);
      }

      return result;
   }

   /*
    * Allocation of registers.
    * 255 and 0 to 9 are always free for scratch purposes. 
    * \count10 to \count20 hold the register numbers that were most
    * recently allocated.
    */

   public void allocCount(int index, CountRegister reg)
   {
      allocCount(Integer.valueOf(index), reg);
   }

   public void allocCount(Integer index, CountRegister reg)
   {
      countAlloc.put(index, reg);
      reg.setAllocation(index.intValue());
   }

   public void allocCount(CountRegister reg)
   {
      // Get the most recently allocated number

      CountRegister allocReg = countAlloc.get(ALLOC_COUNT);

      int alloc = allocReg.getValue()+1;

      while (countAlloc.containsKey(Integer.valueOf(alloc)))
      {
         alloc++;
      }

      allocCount(alloc, reg);
      allocReg.setValue(alloc);
      countAlloc.get(ALLOC_NUMBER).setValue(alloc);
   }

   public void allocDimen(int index, DimenRegister reg)
   {
      allocDimen(Integer.valueOf(index), reg);
   }

   public void allocDimen(Integer index, DimenRegister reg)
   {
      dimenAlloc.put(index, reg);
      reg.setAllocation(index.intValue());
   }

   public void allocDimen(DimenRegister reg)
   {
      // Get the most recently allocated number

      CountRegister allocReg = countAlloc.get(ALLOC_DIMEN);

      int alloc = allocReg.getValue()+1;

      while (countAlloc.containsKey(Integer.valueOf(alloc)))
      {
         alloc++;
      }

      allocDimen(alloc, reg);
      allocReg.setValue(alloc);
      countAlloc.get(ALLOC_NUMBER).setValue(alloc);
   }

   public void allocToken(int index, TokenRegister reg)
   {
      allocToken(Integer.valueOf(index), reg);
   }

   public void allocToken(Integer index, TokenRegister reg)
   {
      toksAlloc.put(index, reg);
      reg.setAllocation(index.intValue());
   }

   public void allocToken(TokenRegister reg)
   {
      // Get the most recently allocated number

      CountRegister allocReg = countAlloc.get(ALLOC_TOKS);

      int alloc = allocReg.getValue()+1;

      while (countAlloc.containsKey(Integer.valueOf(alloc)))
      {
         alloc++;
      }

      allocToken(alloc, reg);
      allocReg.setValue(alloc);
      countAlloc.get(ALLOC_NUMBER).setValue(alloc);
   }

   public TokenRegister getTokenRegister(Pattern p)
   {
      for (Iterator<Integer> it = toksAlloc.keySet().iterator();
           it.hasNext(); )
      {
         Integer key = it.next();
         TokenRegister reg = toksAlloc.get(key);

         if (p.matcher(reg.getName()).matches())
         {
            return reg;
         }
      }

      return null;
   }

   public DimenRegister getDimenRegister(String name)
   {
      for (Iterator<Integer> it = dimenAlloc.keySet().iterator();
           it.hasNext(); )
      {
         Integer key = it.next();
         DimenRegister reg = dimenAlloc.get(key);

         if (name.equals(reg.getName()))
         {
            return reg;
         }
      }

      return null;
   }

   public DimenRegister getDimenRegister(Pattern p)
   {
      for (Iterator<Integer> it = dimenAlloc.keySet().iterator();
           it.hasNext(); )
      {
         Integer key = it.next();
         DimenRegister reg = dimenAlloc.get(key);

         if (p.matcher(reg.getName()).matches())
         {
            return reg;
         }
      }

      return null;
   }

   public CountRegister getCountRegister(Pattern p)
   {
      for (Iterator<Integer> it = countAlloc.keySet().iterator();
           it.hasNext(); )
      {
         Integer key = it.next();
         CountRegister reg = countAlloc.get(key);

         if (p.matcher(reg.getName()).matches())
         {
            return reg;
         }
      }

      return null;
   }

   private TeXSettings settings;

   private Hashtable<Integer,CountRegister> countAlloc
     = new Hashtable<Integer,CountRegister>();

   private Hashtable<Integer,DimenRegister> dimenAlloc
     = new Hashtable<Integer,DimenRegister>();

   private Hashtable<Integer,TokenRegister> toksAlloc
     = new Hashtable<Integer,TokenRegister>();

   public static final Integer ALLOC_COUNT = Integer.valueOf(10);
   public static final Integer ALLOC_DIMEN = Integer.valueOf(11);
   public static final Integer ALLOC_SKIP = Integer.valueOf(12);
   public static final Integer ALLOC_MUSKIP = Integer.valueOf(13);
   public static final Integer ALLOC_BOX = Integer.valueOf(14);
   public static final Integer ALLOC_TOKS = Integer.valueOf(15);
   public static final Integer ALLOC_INPUT = Integer.valueOf(16);
   public static final Integer ALLOC_OUTPUT = Integer.valueOf(17);
   public static final Integer ALLOC_MATHFAM = Integer.valueOf(18);
   public static final Integer ALLOC_LANGUAGE = Integer.valueOf(19);
   public static final Integer INS_COUNT = Integer.valueOf(20);
   public static final Integer ALLOC_NUMBER = Integer.valueOf(21);
   public static final Integer MINUS_ONE = Integer.valueOf(22);

   protected Hashtable<String,ControlSequence> csTable;

   protected Hashtable<Integer,ActiveChar> activeTable;

   private Writer writer;

   private TeXReader reader;

   protected File baseDir = null;

   private TeXParserListener listener;

   public static final int TYPE_ESC = 0, TYPE_BG=1, TYPE_EG=2,
     TYPE_MATH=3, TYPE_TAB=4, TYPE_EOL=5, TYPE_PARAM=6, TYPE_SP=7,
     TYPE_SB=8, TYPE_IGNORE=9, TYPE_SPACE=10, TYPE_LETTER=11,
     TYPE_OTHER=12, TYPE_ACTIVE=13, TYPE_COMMENT=14, TYPE_INVALID=15;

   private CatCodeList[] catcodes;

   private Vector<String> verbatim;

   private String jobname = null;

   private int debugMode = 0;

   private PrintWriter logWriter = null;

   private boolean logging = false;

   private Charset currentInputCharset = null;

   public static final int DEBUG_IO = 1;
   public static final int DEBUG_POPPED = 2;
   public static final int DEBUG_DECL = 4;
   public static final int DEBUG_STY_DATA = 8;
   public static final int DEBUG_EXPANSION = 16;
   public static final int DEBUG_EXPANSION_LIST = 32;
   public static final int DEBUG_PROCESSING = 64;
   public static final int DEBUG_PROCESSING_STACK = 128;
   public static final int DEBUG_PROCESSING_STACK_LIST = 256;
   public static final int DEBUG_CS = 1024;
   public static final int DEBUG_PROCESSING_GENERIC_CS = 2048;
   public static final int DEBUG_EXPANSION_ONCE = 4096;
   public static final int DEBUG_EXPANSION_ONCE_LIST = 8192;
   public static final int DEBUG_CATCODE = 16384;
   public static final int DEBUG_READ = 32768;

   public static final String VERSION = "0.9.8b";
   public static final String VERSION_DATE = "2023-09-29";
}
