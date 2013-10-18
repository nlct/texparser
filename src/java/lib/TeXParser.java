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
import java.io.*;
import java.nio.file.Path;

import com.dickimawbooks.texparserlib.latex.LaTeXSyntaxException;

public class TeXParser extends TeXObjectList
{
   public TeXParser(TeXParserListener listener)
   {
      this.listener = listener;
      reader = null;

      verbatim = new Vector<String>();

      verbatim.add("verb");

      initDefCatCodes();
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

      // add it to its new catcode list
      catcodes[catCode].add(character);
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

         reader.mark(1);

         c = reader.read();

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

            reader.mark(1);

            c = reader.read();

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

               reader.reset();

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
               reader.reset();
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

         reader.mark(1);

         c = reader.read();

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

            reader.mark(1);

            c = reader.read();

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

               reader.reset();

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
            reader.reset();

            eolFound(list);
         }
      }
      else // Neither CR nor LF
      {
         // User has assigned another character the EOL
         // category code

         reader.mark(1);

         c = reader.read();

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

            reader.reset();

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

      reader.mark(1);

      SkippedEols skipped = null;

      while ((c = reader.read()) != -1)
      {
         if (!isCatCode(TYPE_EOL, (char)c))
         {
            reader.reset();
            break;
         }

         if (skipped == null)
         {
            skipped = listener.createSkippedEols();
         }

         Eol eol = listener.getEol();
         eol.setEol(""+(char)c);
         skipped.add(eol);

         reader.mark(1);
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

      reader.mark(1);

      SkippedSpaces skipped = null;

      while ((c = reader.read()) != -1)
      {
         if (!isCatCode(TYPE_SPACE, (char)c))
         {
            reader.reset();
            break;
         }

         if (skipped == null)
         {
            skipped = listener.createSkippedSpaces();
         }

         Space space = listener.getSpace();
         space.setSpace(c);
         skipped.add(space);

         reader.mark(1);
      }

      if (c == -1)
      {
         reader.reset();
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

   private boolean readComment(TeXObjectList list)
     throws IOException
   {
      Comment comment = listener.createComment();

      int c = -1;

      while ((c = reader.read()) != -1)
      {
         if (isCatCode(TYPE_EOL, (char)c))
         {
            if (c == (int)'\n')
            {
               reader.mark(1);
               c = reader.read();

               if (c == (int)'\r')
               {
                  // LF+CR
                  reader.mark(1);
                  c = reader.read();

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
                  reader.reset();
                  return skipNextSpaces(list);
               }
            }
            else if (c == (int)'\r')
            {
               reader.mark(1);
               c = reader.read();

               if (c == (int)'\n')
               {
                  // CR+LF
                  reader.mark(1);
                  c = reader.read();

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
                  reader.reset();
                  return skipNextSpaces(list);
               }
            }
            else
            {
               // EOL

               reader.mark(1);
               c = reader.read();

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
                  reader.reset();
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
      int c = reader.read();

      if (c == -1)
      {
         throw new TeXSyntaxException(getLineNumber(),
            TeXSyntaxException.ERROR_BAD_PARAM, "EOF");
      }

      if (isCatCode(TYPE_PARAM, (char)c))
      {
         c = reader.read();

         if (c > (int)'0' && c <= (int)'9')
         {
            list.add(listener.getDoubleParam(
              listener.getParam(c-(int)'0')));
         }
         else
         {
            throw new TeXSyntaxException(getLineNumber(),
               TeXSyntaxException.ERROR_BAD_PARAM, ""+((int)c));
         }
      }
      else
      {
         if (c >= (int)'0' && c <= (int)'9')
         {
            list.add(listener.getParam(c-(int)'0'));
         }
         else
         {
            throw new TeXSyntaxException(getLineNumber(),
               TeXSyntaxException.ERROR_BAD_PARAM, ""+((int)c));
         }
      }

      return true;
   }

   private boolean readGroup(Group group, boolean isShort)
     throws IOException
   {
      int c;

      reader.mark(1);

      while ((c = reader.read()) != -1)
      {
         if (isCatCode(TYPE_EG, (char)c))
         {
            return true;
         }

         reader.reset();

         fetchNext(group, isShort);

         if (isShort && (group.lastElement() instanceof Par))
         {
            throw new TeXSyntaxException(getLineNumber(),
               TeXSyntaxException.ERROR_PAR_BEFORE_EG);
         }

         reader.mark(1);
      }

      if (c == -1)
      {
         throw new TeXSyntaxException(getLineNumber(),
            TeXSyntaxException.ERROR_NO_EG);
      }

      return c != -1;
   }

   private void readMath(MathGroup math)
     throws IOException
   {
      reader.mark(1);
      int c = reader.read();

      if (c == -1)
      {
         throw new TeXSyntaxException(getLineNumber(),
            TeXSyntaxException.ERROR_MISSING_ENDMATH);
      }
      else if (isCatCode(TYPE_MATH, (char)c))
      {
         math.setInLine(false);
         readDisplayMath(math);
      }
      else
      {
         reader.reset();
         math.setInLine(true);
         readInLineMath(math);
      }
   }

   private void readInLineMath(MathGroup math)
     throws IOException
   {
      reader.mark(1);
      int c;

      while ((c = reader.read()) != -1)
      {
         if (isCatCode(TYPE_MATH, (char)c))
         {
            return;
         }

         reader.reset();

         fetchNext(math, true);

         reader.mark(1);
      }

      throw new EOFException();
   }

   private void readDisplayMath(MathGroup math)
     throws IOException
   {
      reader.mark(1);
      int c;

      while ((c = reader.read()) != -1)
      {
         if (isCatCode(TYPE_MATH, (char)c))
         {
            reader.mark(1);
            c = reader.read();

            if (c == -1)
            {
               throw new TeXSyntaxException(getLineNumber(),
                  TeXSyntaxException.ERROR_MISSING_ENDMATH);
            }

            if (!isCatCode(TYPE_MATH, (char)c))
            {
               reader.reset();
               throw new TeXSyntaxException(getLineNumber(),
                  TeXSyntaxException.ERROR_DOLLAR2_ENDED_WITH_DOLLAR);
            }

            return;
         }

         reader.reset();

         fetchNext(math, true);

         reader.mark(1);
      }

      throw new EOFException();
   }

   public void readTo(String terminator, TeXObjectList list)
     throws IOException
   {
      int n = terminator.length();
      reader.mark(n);

      readTo(terminator, list, n, 0);
   }

   private void readTo(String terminator, TeXObjectList list,
     int n, int idx)
     throws IOException
   {
      int c = reader.read();

      if (c == -1)
      {
         throw new TeXSyntaxException(getLineNumber(),
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
         reader.reset();

         if (!fetchNext(list))
         {
            throw new TeXSyntaxException(getLineNumber(),
               TeXSyntaxException.ERROR_NOT_FOUND, terminator);
         }

         reader.mark(n);

         readTo(terminator, list, n, 0);
      }
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

      int c = reader.read();

      if (c == -1) return false;

      if (isCatCode(TYPE_EOL, (char)c))
      {
         parseEOL(c, list);
      }
      else if (isCatCode(TYPE_ESC, (char)c))
      {
         StringBuilder macro = new StringBuilder();

         c = -1;
         reader.mark(1);

         while ((c = reader.read()) != -1)
         {
            if (!isLetter((char)c))
            {
               ControlSequence cs;

               if (isCatCode(TYPE_EOL, (char)c))
               {
                  // Control sequence ended with EOL

                  if (macro.length() == 0)
                  {
                     cs = listener.getControlSequence(" ");
                  }
                  else
                  {
                     cs = listener.getControlSequence(macro.toString());
                  }

                  list.add(cs);

                  parseEOL(c, list);
               }
               else if (macro.length() == 0)
               {
                  // Control Symbol

                  cs = listener.getControlSequence(""+(char)c);

                  list.add(cs);
               }
               else if (isCatCode(TYPE_SPACE, (char)c))
               {
                  // Control word ended by a space

                  cs = listener.getControlSequence(macro.toString());
                  list.add(cs);

                  if (!skipNextSpaces(list))
                  {
                     return false;
                  }
               }
               else
               {
                  // Control word ended by non-space

                  reader.reset();
                  cs = listener.getControlSequence(macro.toString());
                  list.add(cs);
               }

               if (isVerbCommand(cs.getName()))
               {
                  c = reader.read();

                  if (c == (int)'*')
                  {
                     list.add(new Other(c));
                     c = reader.read();
                  }

                  int delim = c;

                  TeXObjectList charList = new TeXObjectList();
                  list.add(charList);

                  charList.add(new Other(c));

                  while (c != -1)
                  {
                     c = reader.read();

                     charList.add(new Other(c));

                     if (c == delim)
                     {
                        break;
                     }
                  }
               }

               return true;
            }

            reader.mark(1);
            macro.appendCodePoint(c);    
         }

         if (c == -1)
         {
            list.add(listener.getControlSequence(" "));
            return false;
         }

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
         throw new TeXSyntaxException(getLineNumber(),
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
         reader.mark(1);
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
      this.reader = reader;

      settings.setMode(mode);

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

   public TeXObject popStack()
     throws IOException
   {
      if (size() == 0)
      {
         fetchNext();
      }

      if (size() == 0)
      {
         throw new EOFException();
      }

      TeXObject object = remove(0);

      if (object instanceof Ignoreable)
      {
         listener.skipping(this, (Ignoreable)object);

         return popStack();
      }

      return object;
   }

   public TeXObject peekStack()
   {
      return size() == 0 ? null : firstElement();
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
      int orgLineNum = currentLineNum;
      File orgParentFile = currentParentFile;
      resetLineNum();

      currentParentFile = file.getParentFile();

      try
      {
         listener.beginParse(this, file);
         parse(new LineNumberReader(new FileReader(file)));
      }
      catch (EOFException e)
      {
      }
      finally
      {
         currentParentFile = orgParentFile;
         resetLineNum(orgLineNum);
         listener.endParse(this, file);
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

   public boolean isMathMode()
   {
      return settings.getMode() != TeXSettings.MODE_TEXT;
   }

   public void startGroup()
   {
      settings = new TeXSettings(settings);
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

   private TeXSettings settings = new TeXSettings();

   private File currentParentFile;

   private Reader reader;

   private Writer writer;

   private TeXParserListener listener;

   private int currentLineNum = -1;

   public static final int TYPE_ESC = 0, TYPE_BG=1, TYPE_EG=2,
     TYPE_MATH=3, TYPE_TAB=4, TYPE_EOL=5, TYPE_PARAM=6, TYPE_SP=7,
     TYPE_SB=8, TYPE_IGNORE=9, TYPE_SPACE=10, TYPE_LETTER=11,
     TYPE_OTHER=12, TYPE_ACTIVE=13, TYPE_COMMENT=14, TYPE_INVALID=15;

   private CatCodeList[] catcodes;

   private Vector<String> verbatim;
}
