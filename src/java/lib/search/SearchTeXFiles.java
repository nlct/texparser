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

package com.dickimawbooks.texparserlib.search;

import java.io.*;
import java.util.Vector;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.latex.*;

/**
 * Search for commands or environments within a LaTeX document.
 * A simple grep style pattern search may pick up commented lines
 * or commands/environments within the definition of something 
 * that doesn't get expanded or within the false part of conditionals.
 * The search here is intended only for commands or environments 
 * that are actually used. Naturally, since the TeX parser library isn't
 * an actual TeX engine and it's not parsing packages, it's possible that
 * some instances may be missed if they're embedded in commands or environments
 * that the parser doesn't recognise.
 *
 * Not intended for internal commands.
 */
public class SearchTeXFiles extends LaTeXParserListener
  implements Writeable
{
   public SearchTeXFiles(TeXApp texApp)
   {
      super(null, false);// don't parse aux or packages

      this.texApp = texApp;
      setWriteable(this);

      searchMatchers = new Vector<SearchMatcher>();
      searchResults = new Vector<SearchResult>();
   }

   public SearchTeXFiles()
   {
      this(new TeXAppAdapter() // silent, no write
      {
         public void message(String text) { }
         public void warning(TeXParser parser, String text) { }
         public void error(Exception e) { }

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
   }

   public void setStopOnNextMatch(boolean stop)
   {
      stopOnNextMatch = stop;
   }

   public boolean isStopOnNextMatchOn()
   {
      return stopOnNextMatch;
   }

   public void addMatcher(SearchMatcher matcher)
   {
      searchMatchers.add(matcher);
   }

   public Vector<SearchResult> getResults()
   {
      return searchResults;
   }

   public void clearResults()
   {
      searchResults.clear();
   }

   public ControlSequence getControlSequence(String name)
   {
      ControlSequence cs = super.getControlSequence(name);

      if (cs instanceof SearchControlSequence)
      {
         return cs;
      }

      for (SearchMatcher matcher : searchMatchers)
      {
         if (matcher.isMatch(cs))
         {
            SearchControlSequence scs = new SearchControlSequence(cs,
                  matcher, this);

            parser.putControlSequence(scs);

            return scs;
         }
      }

      return cs;
   }

   public void beginParse(File file, Charset encoding)
    throws IOException
   {
   }

   public void endParse(File file)
    throws IOException
   {
   }

   public void addSearchResultListener(SearchResultListener listener)
   {
      if (searchResultListeners == null)
      {
         searchResultListeners = new Vector<SearchResultListener>();
      }

      searchResultListeners.add(listener);
   }

   public boolean removeSearchResultListener(SearchResultListener listener)
   {
      if (searchResultListeners == null)
      {
         return false;
      }

      return searchResultListeners.remove(listener);
   }

   public void registerMatch(SearchObject object) throws SearchTerminatedException
   {
      SearchResult result = new SearchResult(parser, object);

      if (searchResultListeners != null)
      {
         for (SearchResultListener listener : searchResultListeners)
         {
            listener.matchFound(result);
         }
      }

      searchResults.add(result);

      if (stopOnNextMatch)
      {
         throw new SearchTerminatedException();
      }
   }

   public void includegraphics(KeyValList options, String imgName)
    throws IOException
   {
      File imageFile = getImageFile(imgName);

      for (SearchMatcher matcher : searchMatchers)
      {
         if (matcher.isMatch(imageFile))
         {
            registerMatch(new SearchFileName(imageFile, matcher));
            return;
         }
      }
   }

   public void href(String url, TeXObject text)
     throws IOException
   {
      try
      {
         URI uri = new URI(url);

         for (SearchMatcher matcher : searchMatchers)
         {
            if (matcher.isMatch(uri))
            {
               registerMatch(new SearchURI(uri, matcher));
               return;
            }
         }
      }
      catch (URISyntaxException e)
      {
         texApp.error(e);
      }

      text.process(parser);
   }

   public void abovewithdelims(TeXObject firstDelim,
     TeXObject secondDelim, TeXDimension thickness,
     TeXObject before, TeXObject after)
    throws IOException
   {
      if (firstDelim != null)
      {
         firstDelim.process(parser);
      }

      before.process(parser);

      after.process(parser);

      if (secondDelim != null)
      {
         secondDelim.process(parser);
      }
   }

   public void overwithdelims(TeXObject firstDelim,
     TeXObject secondDelim, TeXObject before, TeXObject after)
    throws IOException
   {
      if (firstDelim != null)
      {
        firstDelim.process(parser);
      }

      before.process(parser);

      if (secondDelim != null)
      {
         secondDelim.process(parser);
      }
   }

   public void superscript(TeXObject arg)
    throws IOException
   {
      arg.process(parser);
   }

   public void subscript(TeXObject arg)
    throws IOException
   {
      arg.process(parser);
   }

   public void substituting(String original, String replacement)
   {
      texApp.substituting(parser, original, replacement);
   }

   public void skipping(Ignoreable ignoreable)
     throws IOException
   {
   }

   public void writeln(String text)
   {
   }

   public void write(String text)
   {
   }

   public void write(char c)
   {
   }

   public void writeCodePoint(int cp)
   {
   }

   public TeXApp getTeXApp()
   {
      return texApp;
   }

   private Vector<SearchResultListener> searchResultListeners;
   private Vector<SearchMatcher> searchMatchers;
   private Vector<SearchResult> searchResults;
   private boolean stopOnNextMatch=false;
   private TeXApp texApp;
}
