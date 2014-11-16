/*
    Copyright (C) 2013 Nicola L.C. Talbot
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
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.io.*;
import java.nio.file.Path;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.file.Files;

import com.dickimawbooks.texparserlib.latex.LaTeXSyntaxException;

public class TeXParser extends TeXObjectList
{
   public TeXParser(TeXParserListener listener)
   {
      this.listener = listener;
      reader = null;
      parentReader = null;
      markedReader = null;

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

      catcodes[TYPE_ESC].add(new Character('\\'));
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

      for (int i = (int)'A'; i <= (int)'Z'; i++)
      {
         catcodes[TYPE_LETTER].add(new Character((char)i));
      }

      for (int i = (int)'a'; i <= (int)'z'; i++)
      {
         catcodes[TYPE_LETTER].add(new Character((char)i));
      }

      catcodes[TYPE_ACTIVE].add('~');
      catcodes[TYPE_COMMENT].add('%');

   }

   // checks if c has cat code of given type
   public boolean isCatCode(int type, char c)
   {
      Character character = new Character(c);

      if (catcodes[type] != null)
      {
         return catcodes[type].contains(character);
      }

      // Check if it's TYPE_OTHER

      for (int i = 0; i < catcodes.length; i++)
      {
         if (catcodes[i] != null && catcodes[i].contains(character))
         {
            return false;
         }
      }

      return true;
   }

   public void setCatCode(char c, int catCode)
   {
      Character character = new Character(c);

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
   public int getCatCode(char c)
   {
      Character character = new Character(c);

      for (int i = 0; i < catcodes.length; i++)
      {
         if (catcodes[i] != null && catcodes[i].contains(character))
         {
            return i;
         }
      }

      return TYPE_OTHER;
   }

   public int getLineNumber()
   {
      if (reader == null || !(reader instanceof LineNumberReader))
      {
         return currentLineNum;
      }

      currentLineNum = ((LineNumberReader)reader).getLineNumber()+1;

      return currentLineNum;
   }

   public boolean isLetter(char c)
   {
      return isCatCode(TYPE_LETTER, c);
   }

   private int read() throws IOException
   {
      int c = reader.read();

      if (c == -1 && parentReader != null)
      {
         reader = parentReader;
         parentReader = null;
         c = reader.read();
      }

      return c;
   }

   private void mark(int limit) throws IOException
   {
      reader.mark(limit);
      markedReader = reader;
   }

   private void reset() throws IOException
   {
      if (markedReader != reader)
      {
         parentReader = reader;
         reader = markedReader;
      }

      reader.reset();
      markedReader = null;
   }

   public void scan(String text, TeXObjectList stack)
     throws IOException
   {
      Reader orgParentReader = parentReader;
      parentReader = reader;

      StringReader strReader = new StringReader(text);
      reader = strReader;

      try
      {
         while (reader == strReader)
         {
            if (!fetchNext(stack))
            {
               break;
            }
         }
      }
      catch (EOFException e)
      {
      }

      reader = parentReader;
      parentReader = orgParentReader;
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

         while (isCatCode(TYPE_SPACE, (char)c))
         {
            c = read();
         }

         if (isCatCode(TYPE_EOL, (char)c))
         {
            parseEOL(c, this);
            text = pop().toString(this);
         }
         else if (isCatCode(TYPE_ESC, (char)c))
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

      StringCharacterIterator it = new StringCharacterIterator(text);

      for (char c = it.first(); c != CharacterIterator.DONE; c = it.next())
      {
         list.add(isLetter(c) ? new Letter(c) : new Other(c));
      }

      return list;
   }

   public void addVerbCommand(String csname)
   {
      verbatim.add(csname);
   }

   public boolean isVerbCommand(String csname)
   {
      return verbatim.contains(csname);
   }

   private boolean parseEOL(int c, TeXObjectList list)
     throws IOException
   {
      boolean isNotEof = true;

      if (c == '\n')
      {
         // Is this just LF or is it LF+CR?
         // Or is it \n\n (paragraph break?)

         mark(1);

         c = read();

         if (c == (int)'\n')
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

            if (isCatCode(TYPE_EOL, (char)c))
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

               eolFound(list);
            }
         }
         else if (isCatCode(TYPE_EOL, (char)c))
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

            eolFound(list);
         }
      }
      else if (c == '\r')
      {
         // Is this just CR or is it CR+LF?
         // Or is it \r\r (paragraph break?)

         mark(1);

         c = read();

         if (c == (int)'\r')
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

            if (isCatCode(TYPE_EOL, (char)c))
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

               eolFound(list);
            }
         }
         else if (isCatCode(TYPE_EOL, (char)c))
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

            eolFound(list);
         }
      }
      else // Neither CR nor LF
      {
         // User has assigned another character the EOL
         // category code

         mark(1);

         c = read();

         // Do we have a paragraph break?

         if (isCatCode(TYPE_EOL, (char)c))
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

            eolFound(list);
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
         if (!isCatCode(TYPE_EOL, (char)c))
         {
            reset();
            break;
         }

         if (skipped == null)
         {
            skipped = listener.createSkippedEols();
         }

         Eol eol = listener.getEol();
         eol.setEol(""+(char)c);
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

      while ((c = read()) != -1)
      {
         if (!isCatCode(TYPE_SPACE, (char)c))
         {
            reset();
            break;
         }

         if (skipped == null)
         {
            skipped = listener.createSkippedSpaces();
         }

         Space space = listener.getSpace();
         space.setSpace(c);
         skipped.add(space);

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

   private void eolFound(TeXObjectList list)
     throws IOException
   {
      list.add(listener.getEol());
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
         if (isCatCode(TYPE_EOL, (char)c))
         {
            if (c == (int)'\n')
            {
               mark(1);
               c = read();

               if (c == (int)'\r')
               {
                  // LF+CR
                  mark(1);
                  c = read();

                  if (isCatCode(TYPE_EOL, (char)c))
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
               else if (c == (int)'\n')
               {
                  // LF LF

                  list.add(comment);
                  list.add(listener.getPar());

                  return skipNextEols(list);
               }
               else if (isCatCode(TYPE_EOL, (char)c))
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
            else if (c == (int)'\r')
            {
               mark(1);
               c = read();

               if (c == (int)'\n')
               {
                  // CR+LF
                  mark(1);
                  c = read();

                  if (isCatCode(TYPE_EOL, (char)c))
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
               else if (c == (int)'\r')
               {
                  // CR + CR

                  list.add(comment);
                  list.add(listener.getPar());

                  return skipNextEols(list);
               }
               else if (isCatCode(TYPE_EOL, (char)c))
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

               if (isCatCode(TYPE_EOL, (char)c))
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

   private boolean readParam(TeXObjectList list)
     throws IOException
   {
      mark(1);
      int c = read();

      if (c == -1)
      {
         throw new TeXSyntaxException(
            getListenerFile(), getLineNumber(),
            TeXSyntaxException.ERROR_BAD_PARAM, "EOF");
      }

      if (isCatCode(TYPE_PARAM, (char)c))
      {
         mark(1);
         c = read();

         if (c > (int)'0' && c <= (int)'9')
         {
            list.add(listener.getDoubleParam(
              listener.getParam(c-(int)'0')));
         }
         else if (isCatCode(TYPE_BG, (char)c))
         {
            list.add(listener.getDoubleParam(
              listener.getParam(-1)));
            reset();
         }
         else
         {
            throw new TeXSyntaxException(
               getListenerFile(),
               getLineNumber(),
               TeXSyntaxException.ERROR_BAD_PARAM, ""+((int)c));
         }
      }
      else
      {
         if (c >= (int)'0' && c <= (int)'9')
         {
            list.add(listener.getParam(c-(int)'0'));
         }
         else if (isCatCode(TYPE_BG, (char)c))
         {
            list.add(listener.getParam(-1));
            reset();
         }
         else
         {
            throw new TeXSyntaxException(
               getListenerFile(),
               getLineNumber(),
               TeXSyntaxException.ERROR_BAD_PARAM, ""+((int)c));
         }
      }

      return true;
   }

   private boolean readGroup(Group group, boolean isShort)
     throws IOException
   {
      int c;

      mark(1);

      while ((c = read()) != -1)
      {
         if (isCatCode(TYPE_EG, (char)c))
         {
            return true;
         }

         reset();

         fetchNext(group, isShort);

         if (isShort && isPar(group.lastElement()))
         {
            throw new TeXSyntaxException(
               getListenerFile(),
               getLineNumber(),
               TeXSyntaxException.ERROR_PAR_BEFORE_EG);
         }

         mark(1);
      }

      if (c == -1)
      {
         throw new TeXSyntaxException(
            getListenerFile(),
            getLineNumber(),
            TeXSyntaxException.ERROR_NO_EG);
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
         throw new TeXSyntaxException(
            getListenerFile(),
            getLineNumber(),
            TeXSyntaxException.ERROR_MISSING_ENDMATH);
      }
      else if (isCatCode(TYPE_MATH, (char)c))
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

      while ((c = read()) != -1)
      {
         if (isCatCode(TYPE_MATH, (char)c))
         {
            return;
         }

         reset();

         fetchNext(math, true);

         mark(1);
      }

      throw new EOFException();
   }

   private void readDisplayMath(MathGroup math)
     throws IOException
   {
      mark(1);
      int c;

      while ((c = read()) != -1)
      {
         if (isCatCode(TYPE_MATH, (char)c))
         {
            mark(1);
            c = read();

            if (c == -1)
            {
               throw new TeXSyntaxException(
                  getListenerFile(),
                  getLineNumber(),
                  TeXSyntaxException.ERROR_MISSING_ENDMATH);
            }

            if (!isCatCode(TYPE_MATH, (char)c))
            {
               reset();
               throw new TeXSyntaxException(
                  getListenerFile(),
                  getLineNumber(),
                  TeXSyntaxException.ERROR_DOLLAR2_ENDED_WITH_DOLLAR);
            }

            return;
         }

         reset();

         fetchNext(math, true);

         mark(1);
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
         throw new TeXSyntaxException(
            getListenerFile(),
            getLineNumber(),
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
            throw new TeXSyntaxException(
               getListenerFile(),
               getLineNumber(),
               TeXSyntaxException.ERROR_NOT_FOUND, terminator);
         }

         mark(n);

         readTo(terminator, list, n, 0);
      }
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
         if (!isLetter((char)c))
         {
            TeXCsRef cs;

            if (isCatCode(TYPE_EOL, (char)c))
            {
               // Control sequence ended with EOL

               if (macro.length() == 0)
               {
                  cs = new TeXCsRef(" ");
               }
               else
               {
                  cs = new TeXCsRef(macro.toString());
               }

               list.add(cs);

               parseEOL(c, list);
            }
            else if (macro.length() == 0)
            {
               // Control Symbol

               cs = new TeXCsRef(""+(char)c);

               list.add(cs);
            }
            else if (isCatCode(TYPE_SPACE, (char)c))
            {
               // Control word ended by a space

               cs = new TeXCsRef(macro.toString());
               list.add(cs);

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

               if (c == (int)'*')
               {
                  list.add(new Other(c));
                  c = read();
               }

               int delim = c;

               TeXObjectList charList = new TeXObjectList();
                  list.add(charList);

               charList.add(new Other(c));

               while (c != -1)
               {
                  c = read();

                  charList.add(new Other(c));

                  if (c == delim)
                  {
                     break;
                  }
               }
            }
            else if (cs.getName().equals("string"))
            {
               mark(1);
               c = read();

               while (isCatCode(TYPE_SPACE, (char)c))
               {
                  mark(1);
                  c = read();
               }

               if (isCatCode(TYPE_ESC, (char)c))
               {
                  reset();
               }
               else if (isCatCode(TYPE_LETTER, (char)c))
               {
                  list.add(new Letter(c));
               }
               else
               {
                  list.add(new Other(c));
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
      return fetchNext(this, false);
   }

   public boolean fetchNext(TeXObjectList list, boolean isShort)
     throws IOException
   {
      if (reader == null)
      {
         return false;
      }

      int c = read();

      if (c == -1) return false;

      if (isCatCode(TYPE_EOL, (char)c))
      {
         parseEOL(c, list);
      }
      else if (isCatCode(TYPE_ESC, (char)c))
      {
        return readControlSequence(list);
      }
      else if (isCatCode(TYPE_COMMENT, (char)c))
      {
         return readComment(list);
      }
      else if (isCatCode(TYPE_PARAM, (char)c))
      {
         return readParam(list);
      }
      else if (isCatCode(TYPE_ACTIVE, (char)c))
      {
         list.add(listener.getActiveChar(c));
      }
      else if (isCatCode(TYPE_SP, (char)c))
      {
         list.add(listener.createSpChar());
      }
      else if (isCatCode(TYPE_SB, (char)c))
      {
         list.add(listener.createSbChar());
      }
      else if (isCatCode(TYPE_TAB, (char)c))
      {
         list.add(listener.getTab());
      }
      else if (isCatCode(TYPE_MATH, (char)c))
      {
         MathGroup math = listener.createMathGroup();
         list.add(math);
         readMath(math);
      }
      else if (isCatCode(TYPE_BG, (char)c))
      {
         Group group = listener.createGroup();
         list.add(group);
         return readGroup(group, isShort);
      }
      else if (isCatCode(TYPE_EG, (char)c))
      {
         throw new TeXSyntaxException(
            getListenerFile(),
            getLineNumber(),
            TeXSyntaxException.ERROR_UNEXPECTED_EG);
      }
      else if (isCatCode(TYPE_SPACE, (char)c))
      {
         Space space = listener.getSpace();
         space.setSpace(c);
         list.add(space);
         return skipNextSpaces(list);
      }
      else if (isLetter((char)c))
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

   public void parse(Reader reader)
     throws IOException
   {
      parse(reader, TeXSettings.INHERIT);
   }

   public void parse(Reader reader, int mode)
     throws IOException
   {
      Reader orgParentReader = this.parentReader;
      this.parentReader = this.reader;
      this.reader = reader;

      settings.setMode(mode);

      try
      {
         while (fetchNext())
         {
            if (size() == 0)
            {
               break;
            }

            while (size() > 0)
            {
               TeXObject object = pop();

               try
               {
                  object.process(this);
               }
               catch (EOFException e)
               {
                  return;
               }
               catch (TeXSyntaxException e)
               {
                  listener.getTeXApp().error(e);
               }
               catch (LaTeXSyntaxException e)
               {
                  listener.getTeXApp().error(e);
               }
            }
         }
      }
      finally
      {
         this.parentReader = orgParentReader;
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
      return popNextArg(false);
   }

   public TeXObject popNextArg(boolean isShort)
     throws IOException
   {
      if (size() == 0)
      {
         fetchNext(isShort);
      }

      return super.popArg();
   }

   public TeXObject popNextArg(char openDelim, char closeDelim)
     throws IOException
   {
      return popNextArg(false, openDelim, closeDelim);
   }

   public TeXObject popNextArg(boolean isShort, char openDelim, char closeDelim)
     throws IOException
   {
      if (size() == 0)
      {
         fetchNext(isShort);
      }

      return popArg(this, openDelim, closeDelim);
   }

   public TeXObject popStack() throws IOException
   {
      return popStack(false);
   }

   public TeXObject popStack(boolean isShort)
     throws IOException
   {
      if (size() == 0)
      {
         fetchNext(isShort);
      }

      if (size() == 0)
      {
         throw new EOFException();
      }

      TeXObject object = remove(0);

      if (object instanceof Ignoreable)
      {
         listener.skipping((Ignoreable)object);

         return popStack(isShort);
      }

      return object;
   }

   public TeXObjectList popToGroup(boolean isShort)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      while (true)
      {
         if (size() == 0)
         {
            fetchNext(isShort);
         }

         if (size() == 0)
         {
            throw new EOFException();
         }

         TeXObject obj = firstElement();

         if (obj instanceof Group)
         {
            break;
         }

         obj = remove(0);

         if (obj instanceof Ignoreable)
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

   public TeXObject peekStack()
     throws IOException
   {
      int idx = 0;

      if (size() == 0)
      {
         fetchNext();
      }

      TeXObject obj = firstElement();

      while (obj instanceof Ignoreable)
      {
         idx++;

         if (size() == idx)
         {
            fetchNext();
         }

         obj = get(idx);
      }

      return obj;
   }

   public TeXObject peekStack(int index)
   {
      return size() <= index ? null : get(index);
   }

   public void resetLineNum()
   {
      resetLineNum(-1);
   }

   public void resetLineNum(int number)
   {
      currentLineNum  = number;
   }

   public void parse(File file)
     throws IOException
   {
      parse(file, null);
   }

   public void parse(File file, Charset charset)
     throws IOException
   {
      if (jobname == null)
      {
         jobname = file.getName();

         int idx = jobname.lastIndexOf(".");

         if (idx > 0)
         {
            jobname = jobname.substring(0, idx);
         }
      }

      int orgLineNum = currentLineNum;
      File orgParentFile = currentParentFile;
      resetLineNum();

      currentParentFile = file.getParentFile();

      try
      {
         listener.beginParse(file);

         if (charset == null)
         {
            parse(new LineNumberReader(new FileReader(file)));
         }
         else
         {
            parse(new LineNumberReader(Files.newBufferedReader(file.toPath(), charset)));
         }
      }
      catch (EOFException e)
      {
      }
      finally
      {
         listener.endParse(file);
         currentParentFile = orgParentFile;
         resetLineNum(orgLineNum);

         if (reader != null)
         {
            reader.close();
            reader = null;
         }
      }
   }

   public void setWriter(Writer writer)
   {
      this.writer = writer;
   }

   public Writer getWriter()
   {
      return writer;
   }

   public Reader getReader()
   {
      return reader;
   }

   public void setReader(Reader reader)
   {
      this.reader = reader;
   }

   public TeXParserListener getListener()
   {
      return listener;
   }

   public File getListenerFile()
   {
      return listener == null ? null : listener.getFile();
   }

   public boolean isMathMode()
   {
      return settings.getMode() != TeXSettings.MODE_TEXT;
   }

   public void putControlSequence(ControlSequence cs)
   {
      csTable.put(cs.getName(), cs);

      if (cs instanceof Declaration && isLetter(cs.getName().charAt(0)))
      {
         EndDeclaration endDec = new EndDeclaration(cs.getName());
         csTable.put(endDec.getName(), endDec);
      }
   }

   public void putControlSequence(boolean isLocal, ControlSequence cs)
   {
      if (isLocal)
      {
         settings.putControlSequence(cs);

         if (cs instanceof Declaration)
         {
            EndDeclaration endDec = new EndDeclaration(cs.getName());
            settings.putControlSequence(endDec);
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
         return cs;
      }

      cs = csTable.get(name);

      return cs;
   }

   public void putActiveChar(ActiveChar activeChar)
   {
      activeTable.put(new Integer((int)activeChar.getChar().charValue()),
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
      Integer intCode = new Integer(charCode);

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
   {
      settings = settings.getParent();
   }

   public TeXSettings getSettings()
   {
      return settings;
   }

   public char getEscChar()
   {
      return catcodes[TYPE_ESC].firstElement().charValue();
   }

   public char getMathChar()
   {
      return catcodes[TYPE_MATH].firstElement().charValue();
   }

   public String getMathDelim(boolean isinline)
   {
      char c = catcodes[TYPE_MATH].firstElement().charValue();

      return isinline ? ""+c : ""+c+c;
   }

   public char getCommentChar()
   {
      return catcodes[TYPE_COMMENT].firstElement().charValue();
   }

   public char getBgChar()
   {
      return catcodes[TYPE_BG].firstElement().charValue();
   }

   public char getEgChar()
   {
      return catcodes[TYPE_EG].firstElement().charValue();
   }

   public char getParamChar()
   {
      return catcodes[TYPE_PARAM].firstElement().charValue();
   }

   public char getSpChar()
   {
      return catcodes[TYPE_SP].firstElement().charValue();
   }

   public char getSbChar()
   {
      return catcodes[TYPE_SB].firstElement().charValue();
   }

   public char getTabChar()
   {
      return catcodes[TYPE_TAB].firstElement().charValue();
   }

   public File getCurrentParentFile()
   {
      return currentParentFile;
   }

   public void setCurrentParentFile(File file)
   {
      currentParentFile = file;
   }

   public String getJobname()
   {
      return jobname;
   }

   public String toString()
   {
      StringBuilder builder = new StringBuilder();

      for (TeXObject object : this)
      {
         builder.append(object.toString());
      }

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

   /*
    * Allocation of registers.
    * 255 and 0 to 9 are always free for scratch purposes. 
    * \count10 to \count20 hold the register numbers that were most
    * recently allocated.
    */

   public void allocCount(int index, CountRegister reg)
   {
      allocCount(new Integer(index), reg);
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

      int alloc = allocReg.number()+1;

      while (countAlloc.containsKey(new Integer(alloc)))
      {
         alloc++;
      }

      allocCount(alloc, reg);
      allocReg.setValue(alloc);
      countAlloc.get(ALLOC_NUMBER).setValue(alloc);
   }

   public void allocDimen(int index, DimenRegister reg)
   {
      allocDimen(new Integer(index), reg);
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

      int alloc = allocReg.number()+1;

      while (countAlloc.containsKey(new Integer(alloc)))
      {
         alloc++;
      }

      allocDimen(alloc, reg);
      allocReg.setValue(alloc);
      countAlloc.get(ALLOC_NUMBER).setValue(alloc);
   }

   private TeXSettings settings;

   private Hashtable<Integer,CountRegister> countAlloc
     = new Hashtable<Integer,CountRegister>();

   private Hashtable<Integer,DimenRegister> dimenAlloc
     = new Hashtable<Integer,DimenRegister>();

   public static final Integer ALLOC_COUNT = new Integer(10);
   public static final Integer ALLOC_DIMEN = new Integer(11);
   public static final Integer ALLOC_SKIP = new Integer(12);
   public static final Integer ALLOC_MUSKIP = new Integer(13);
   public static final Integer ALLOC_BOX = new Integer(14);
   public static final Integer ALLOC_TOKS = new Integer(15);
   public static final Integer ALLOC_INPUT = new Integer(16);
   public static final Integer ALLOC_OUTPUT = new Integer(17);
   public static final Integer ALLOC_MATHFAM = new Integer(18);
   public static final Integer ALLOC_LANGUAGE = new Integer(19);
   public static final Integer INS_COUNT = new Integer(20);
   public static final Integer ALLOC_NUMBER = new Integer(21);
   public static final Integer MINUS_ONE = new Integer(22);

   protected Hashtable<String,ControlSequence> csTable;

   protected Hashtable<Integer,ActiveChar> activeTable;

   private File currentParentFile;

   private Reader reader, parentReader, markedReader;

   private Writer writer;

   private TeXParserListener listener;

   private int currentLineNum = -1;

   public static final int TYPE_ESC = 0, TYPE_BG=1, TYPE_EG=2,
     TYPE_MATH=3, TYPE_TAB=4, TYPE_EOL=5, TYPE_PARAM=6, TYPE_SP=7,
     TYPE_SB=8, TYPE_IGNORE=9, TYPE_SPACE=10, TYPE_LETTER=11,
     TYPE_OTHER=12, TYPE_ACTIVE=13, TYPE_COMMENT=14, TYPE_INVALID=15;

   private CatCodeList[] catcodes;

   private Vector<String> verbatim;

   private String jobname = null;
}
