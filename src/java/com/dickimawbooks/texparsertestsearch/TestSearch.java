/*
    Copyright (C) 2020 Nicola L.C. Talbot
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

package com.dickimawbooks.texparsertestsearch;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Vector;
import java.util.regex.Pattern;
import java.text.BreakIterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.search.*;

public class TestSearch implements SearchResultListener
{
   public TestSearch()
   {
      searchTeXFiles = new SearchTeXFiles(new TeXAppAdapter()
      {
         public void error(Exception e)
         {
            if (e instanceof TeXSyntaxException)
            {
               System.err.println(((TeXSyntaxException)e).getMessage(this));

               String errTag = ((TeXSyntaxException)e).getErrorTag();

               if (errTag.contains("expected"))
               {
                  e.printStackTrace();
               }
            }
            else
            {
               e.printStackTrace();
            }
         }

         public String requestUserInput(String message)
            throws IOException
         {
            return null;
         }

         public boolean isWriteAccessAllowed(File file)
         {
            return false;
         }
      });

      searchTeXFiles.addSearchResultListener(this);

      filenames = new Vector<String>();

      charset = Charset.defaultCharset();
   }

   public void matchFound(SearchResult result)
   {
      System.out.format("%s %s%n", result, result.getObject().getSearchMatcher());
   }

   public void runTest() throws IOException
   {
      for (String name : filenames)
      {
         TeXParser parser = new TeXParser(searchTeXFiles);
         parser.parse(new TeXReader(new File(name), charset));
      }
   }

   public static void print(String text)
   {
      BreakIterator wb = BreakIterator.getWordInstance();
      wb.setText(text);

      int start = wb.first();
      int j = 0;

      if (start >= LINE_WIDTH)
      {
         System.out.println(text.substring(0, start));
      }
      else if (start > 0)
      {
         System.out.print(text.substring(0, start));
         j = start;
      }

      for (int end = wb.next(); end != BreakIterator.DONE; 
           start = end, end = wb.next())
      {
         j += end-start;

         if (j >= LINE_WIDTH)
         {
            System.out.println();
            j = 0;

            int cp = text.codePointAt(start);

            while (start < end && Character.isWhitespace(cp))
            {
               start += Character.charCount(cp);
               cp = text.codePointAt(start);
            }
         }

         System.out.print(text.substring(start, end));
      }
   }

   public void help()
   {
      System.out.println("testsearch [<option>]+ <filename>+");
      System.out.println();
      print("Search for pattern matches in the LaTeX document identified by <filename>. This isn't the same as using grep. This searches for instances where a command etc is actually processed by the TeX Parser Library. This may not necessarily correspond to where the associated command might be processed by LaTeX and should not be used for internal commands. Commands in comments or in the definitions of other commands that are expanded or processed aren't included.");
      System.out.println();
      System.out.println();
      System.out.println("--command <pattern> (or -c <pattern>)");
      print("Search for commands that are processed or expanded matching the given pattern.");
      System.out.println();

      System.out.println();
      System.out.println("--image <pattern> <match-type> (or -i <pattern> <match-type>)");
      print("Search for included images matching <pattern> where <match-type> is one of:");
      System.out.println();
      System.out.println("full\tFull path name");
      System.out.println("canonical\tCanonical path name");
      System.out.println("leaf\tBase name including extension");
      System.out.println("base\tBase name excluding extension");
      System.out.println("ext\tFile extension");
      System.out.println();

      System.out.println("--url <pattern> <match-type> (or -u <pattern> <match-type)");
      print("Search for external hyperlinks matching <pattern> where <match-type> is one of:");
      System.out.println();
      System.out.println("all\tThe full URL");
      System.out.println("scheme\tThe scheme element");
      System.out.println("user\tThe user info element");
      System.out.println("host\tThe host element");
      System.out.println("port\tThe port element");
      System.out.println("path\tThe path element");
      System.out.println("query\tThe query element");
      System.out.println("fragment\tThe fragment element");
      System.out.println();

      System.out.println("--file <filename> (or -f <filename)");
      System.out.println("Base LaTeX file name.");

      System.out.println();
      System.out.println("--first-match-only\tStop on first match.");
      System.out.println("--all-matches\tFind all matches.");

      System.out.println();
      System.out.println("--help (or -h)\tPrints this help message.");
   }

   public void parseArgs(String[] args) throws InvalidSyntaxException
   {
      for (int i = 0; i < args.length; i++)
      {
         if (args[i].equals("--help") || args[i].equals("-h"))
         {
            help();
            System.exit(0);
         }
         else if (args[i].equals("--first-match-only"))
         {
            searchTeXFiles.setStopOnNextMatch(true);
         }
         else if (args[i].equals("--all-matches"))
         {
            searchTeXFiles.setStopOnNextMatch(false);
         }
         else if (args[i].equals("--encoding"))
         {
            if (i == args.length-1)
            {
               throw new InvalidSyntaxException("Missing argument to "+args[i]);
            }

            try
            {
               charset = Charset.forName(args[++i]);
            }
            catch (Exception e)
            {
               throw new InvalidSyntaxException(
                 "Invalid or unsupported encoding "+args[i], e);
            }
         }
         else if (args[i].equals("--command") || args[i].equals("-c"))
         {
            if (i == args.length-1)
            {
               throw new InvalidSyntaxException("Missing argument to "+args[i]);
            }

            searchTeXFiles.addMatcher(new ControlSequenceMatcher(
             Pattern.compile(args[++i])));
         }
         else if (args[i].equals("--image") || args[i].equals("-i"))
         {
            String argName = args[i];

            if (i >= args.length-2)
            {
               throw new InvalidSyntaxException(
                  String.format("%s requires two arguments", argName));
            }

            Pattern pattern = Pattern.compile(args[++i]);

            String matchType = args[++i];
            int type;

            if (matchType.equals("full"))
            {
               type = FileNameMatcher.FULL_PATH;
            }
            else if (matchType.equals("canonical"))
            {
               type = FileNameMatcher.CANONICAL_PATH;
            }
            else if (matchType.equals("leaf"))
            {
               type = FileNameMatcher.LEAF;
            }
            else if (matchType.equals("base"))
            {
               type = FileNameMatcher.BASE_NAME;
            }
            else if (matchType.equals("ext"))
            {
               type = FileNameMatcher.EXTENSION;
            }
            else
            {
               throw new InvalidSyntaxException(String.format(
                 "Invalid match type '%s' for %s", matchType, argName));
            }

            searchTeXFiles.addMatcher(new FileNameMatcher(pattern, type));
         }
         else if (args[i].equals("--url") || args[i].equals("-u"))
         {
            String argName = args[i];

            if (i >= args.length-2)
            {
               throw new InvalidSyntaxException(
                  String.format("%s requires two arguments", argName));
            }

            Pattern pattern = Pattern.compile(args[++i]);

            String matchType = args[++i];
            int type;

            if (matchType.equals("all"))
            {
               type = URIMatcher.ALL;
            }
            else if (matchType.equals("scheme"))
            {
               type = URIMatcher.SCHEME;
            }
            else if (matchType.equals("user"))
            {
               type = URIMatcher.USER_INFO;
            }
            else if (matchType.equals("host"))
            {
               type = URIMatcher.HOST;
            }
            else if (matchType.equals("port"))
            {
               type = URIMatcher.PORT;
            }
            else if (matchType.equals("path"))
            {
               type = URIMatcher.PATH;
            }
            else if (matchType.equals("query"))
            {
               type = URIMatcher.QUERY;
            }
            else if (matchType.equals("fragment"))
            {
               type = URIMatcher.FRAGMENT;
            }
            else
            {
               throw new InvalidSyntaxException(String.format(
                 "Invalid match type '%s' for %s", matchType, argName));
            }

            searchTeXFiles.addMatcher(new URIMatcher(pattern, type));
         }
         else if (args[i].equals("--file") || args[i].equals("-f"))
         {
            if (i == args.length-1)
            {
               throw new InvalidSyntaxException("Missing argument to "+args[i]);
            }

            filenames.add(args[++i]);
         }
         else if (args[i].startsWith("-"))
         {
            throw new InvalidSyntaxException(
               String.format("Unknown option '%s'. Use --help for help", args[i]));
         }
         else
         {
            filenames.add(args[i]);
         }
      }

      if (filenames.isEmpty())
      {
         throw new InvalidSyntaxException("One or more filenames required");
      }
   }

   public static void main(String[] args)
   {
      TestSearch test = new TestSearch();

      try
      {
         test.parseArgs(args);
         test.runTest();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   private Charset charset;
   private Vector<String> filenames;
   private SearchTeXFiles searchTeXFiles;
   public static final int LINE_WIDTH=60;
}

class InvalidSyntaxException extends Exception
{
   public InvalidSyntaxException(String msg)
   {
      super(msg);
   }

   public InvalidSyntaxException(String msg, Throwable cause)
   {
      super(msg, cause);
   }
}
