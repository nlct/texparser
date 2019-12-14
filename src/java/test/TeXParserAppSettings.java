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
package com.dickimawbooks.texparsertest;

import java.util.Properties;
import java.io.*;
import java.util.Vector;
import java.util.InvalidPropertiesFormatException;
import java.awt.Font;
import java.awt.Color;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Locale;
import java.net.URL;

/**
 * Application settings for texparsertest.
 */
public class TeXParserAppSettings extends Properties
{
   public TeXParserAppSettings(TeXParserApp app)
   {
      super();
      recentFiles = new Vector<String>();
      this.app = app;

      setDefaults();

      setPropertiesPath();
   }

   private void setPropertiesPath()
   {
      String base;

      if (System.getProperty("os.name").toLowerCase().startsWith("win"))
      {
         base = "texparsertest-settings";
      }
      else
      {
         base = ".texparsertest";
      }

      String home = System.getProperty("user.home");

      if (home == null)
      {
         app.debug("No 'user.home' property!");
         return;
      }

      File homeDir = new File(home);

      if (!homeDir.exists())
      {
         app.debug("Home directory '"+home+"' doesn't exist!");
         return;
      }

      propertiesPath = new File(homeDir, base);

      if (propertiesPath.exists())
      {
         if (!propertiesPath.isDirectory())
         {
            app.debug("'"+propertiesPath+"' isn't a directory");
            propertiesPath = null;
            return;
         }
      }
      else
      {
         if (!propertiesPath.mkdir())
         {
            app.debug("Unable to mkdir '"+propertiesPath+"'");
            propertiesPath = null;

            return;
         }
      }
   }

   public void loadProperties()
      throws IOException,InvalidPropertiesFormatException
   {
      if (propertiesPath == null) return;

      File file = new File(propertiesPath, propertiesName);

      BufferedReader reader = null;
      InputStream in = null;

      try
      {
         if (file.exists())
         {
            in = new FileInputStream(file);

            loadFromXML(in);

            in.close();
            in = null;
         }

         file = new File(propertiesPath, recentName);

         if (file.exists())
         {
            reader = new BufferedReader(new FileReader(file));

            recentFiles.clear();

            String line;

            while ((line=reader.readLine()) != null)
            {
               recentFiles.add(line);
            }

            reader.close();
            reader = null;
         }
      }
      finally
      {
         if (in != null)
         {
            in.close();
            in = null;
         }

         if (reader != null)
         {
            reader.close();
            reader = null;
         }
      }
   }

   public void saveProperties()
      throws IOException
   {
      if (propertiesPath == null) return;

      File file = new File(propertiesPath, propertiesName);

      FileOutputStream out = null;

      PrintWriter writer = null;

      try
      {
         out = new FileOutputStream(file);

         storeToXML(out, null);

         out.close();
         out = null;

         file = new File(propertiesPath, recentName);

         writer = new PrintWriter(new FileWriter(file));

         for (String name : recentFiles)
         {
            writer.println(name);
         }

         writer.close();
         writer = null;

      }
      finally
      {
         if (out != null)
         {
            out.close();
            out = null;
         }

         if (writer != null)
         {
            writer.close();
            writer = null;
         }
      }
   }

   public void clearRecentFiles()
   {
      recentFiles.clear();
   }

   public String getRecentFileName(int i)
   {
      return recentFiles.get(i);
   }

   public int getRecentFileCount()
   {
      return recentFiles.size();
   }

   public void addRecentFile(File file)
   {
      String name = file.getAbsolutePath();

      // remove if already in the list

      recentFiles.remove(name);

      // Insert at the start of the list

      recentFiles.add(0, name);
   }

   public void setStartUp(int category)
   {
      if (category < 0 || category > STARTUP_CUSTOM)
      {
         throw new IllegalArgumentException(
           "Invalid startup category "+category);
      }

      setProperty("startup", ""+category);
   }

   public int getStartUp()
   {
      String prop = getProperty("startup");

      if (prop == null)
      {
         setStartUp(STARTUP_HOME);
         return STARTUP_HOME;
      }

      try
      {
         int result = Integer.parseInt(prop);

         if (result < 0 || result > STARTUP_CUSTOM)
         {
            app.debug("Invalid startup setting '"+prop+"'");
            return STARTUP_HOME;
         }

         return result;
      }
      catch (NumberFormatException e)
      {
         app.debug("Invalid startup setting '"+prop+"'");
         return STARTUP_HOME;
      }
   }

   public void directoryOnExit(File file)
   {
      if (getStartUp() == STARTUP_LAST)
      {
         setProperty("startupdir", file.getAbsolutePath());
      }
   }

   public void setCustomStartUp(File file)
   {
      setStartUp(STARTUP_CUSTOM);
      setProperty("startupdir", file.getAbsolutePath());
   }

   public File getStartUpDirectory()
   {
      switch (getStartUp())
      {
         case STARTUP_HOME:
            return new File(System.getProperty("user.home"));
         case STARTUP_CWD:
            return new File(".");
      }

      String name = getProperty("startupdir");

      if (name == null)
      {
         return new File(System.getProperty("user.home"));
      }

      return new File(name);
   }

   public long getMaxProcessTime()
   {
      long maxTime = 1000*60*5;

      try
      {
         return Long.parseLong(getProperty("max_process_time"));
      }
      catch (NumberFormatException e)
      {
         setMaxProcessTime(maxTime);
         return maxTime;
      }
   }

   public void setMaxProcessTime(long maxProcessTime)
   {
      setProperty("max_process_time", ""+maxProcessTime);
   }

   public String getFontName()
   {
      return getProperty("fontname");
   }

   public void setFontName(String name)
   {
      setProperty("fontname", name);
   }

   public int getFontSize()
   {
      try
      {
         return Integer.parseInt(getProperty("fontsize"));
      }
      catch (NumberFormatException e)
      {
         setFontSize(12);
         return 12;
      }
   }

   public void setFontSize(int fontSize)
   {
      setProperty("fontsize", ""+fontSize);
   }

   public Font getFont()
   {
      return new Font(getFontName(), Font.PLAIN, getFontSize());
   }

   public void setSyntaxHighlighting(boolean enable)
   {
      setProperty("syntaxhighlighting", ""+enable);
   }

   public boolean isSyntaxHighlightingOn()
   {
      String prop = getProperty("syntaxhighlighting");

      if (prop == null || prop.isEmpty())
      {
         setSyntaxHighlighting(true);
         return true;
      }

      return Boolean.parseBoolean(prop);
   }

   public Color getControlSequenceHighlight()
   {
      String prop = getProperty("highlightcs");

      if (prop == null)
      {
         setControlSequenceHighlight(Color.BLUE);
         return Color.BLUE;
      }

      try
      {
         return new Color(Integer.parseInt(prop));
      }
      catch (NumberFormatException e)
      {
         setControlSequenceHighlight(Color.BLUE);
         return Color.BLUE;
      }
   }

   public void setControlSequenceHighlight(Color highlight)
   {
      setProperty("highlightcs", ""+highlight.getRGB());
   }

   public Color getCommentHighlight()
   {
      String prop = getProperty("highlightcomment");

      if (prop == null)
      {
         setCommentHighlight(Color.GRAY);
         return Color.GRAY;
      }

      try
      {
         return new Color(Integer.parseInt(prop));
      }
      catch (NumberFormatException e)
      {
         setCommentHighlight(Color.GRAY);
         return Color.GRAY;
      }
   }

   public void setCommentHighlight(Color highlight)
   {
      setProperty("highlightcomment", ""+highlight.getRGB());
   }

   public String getDictionary()
   {
      String prop = getProperty("dictionary");

      if (prop == null)
      {
         Locale locale = Locale.getDefault();

         String language = locale.getLanguage();
         String country = locale.getCountry();

         URL url = getClass().getResource(DICT_DIR + RESOURCE
          + "-" + language + "-" + country  + ".xml");

         if (url == null)
         {
            url = getClass().getResource(DICT_DIR + RESOURCE
              + "-" + language + ".xml");

            if (url == null)
            {
               prop = "en-GB";
            }
            else
            {
               prop = language;
            }
         }
         else
         {
            prop = language+"-"+country;
         }

         setDictionary(prop);
      }

      return prop;
   }

   public void setDictionary(String dictionary)
   {
      setProperty("dictionary", dictionary);
   }

   public String getHelpSet()
   {
      String prop = getProperty("helpset");

      if (prop == null)
      {
         Locale locale = Locale.getDefault();

         String language = locale.getLanguage();
         String country = locale.getCountry();

         String helpsetLocation = HELPSET_DIR+RESOURCE;

         URL hsURL = getClass().getResource(helpsetLocation
          + "-" + language + "-" + country + "/" + RESOURCE + ".hs");
         if (hsURL == null)
         {
            hsURL = getClass().getResource(helpsetLocation
              + "-"+language + "/" + RESOURCE + ".hs");

            if (hsURL == null)
            {
               app.debug("Can't find language file for "
                   +language+"-"+country);
               prop = "en-US";
            }
            else
            {
               prop = language;
            }
         }
         else
         {
            prop = language+"-"+country;
         }

         setHelpSet(prop);
      }
 
      return prop;
   }

   public void setHelpSet(String helpset)
   {
      setProperty("helpset", helpset);
   }

   public static String getHelpSetLocation()
   {
      return HELPSET_DIR + RESOURCE;
   }

   public static String getDictionaryLocation()
   {
      return DICT_DIR + RESOURCE;
   }

   public void setDefaults()
   {
      setStartUp(STARTUP_HOME);
      setFontName("Monospaced");
      setFontSize(12);
   }

   private Vector<String> recentFiles;

   private File propertiesPath = null;

   private final String propertiesName="texparsertest.prop";

   private final String recentName = "recentfiles";

   private TeXParserApp app;

   public static final int STARTUP_HOME   = 0;
   public static final int STARTUP_CWD    = 1;
   public static final int STARTUP_LAST   = 2;
   public static final int STARTUP_CUSTOM = 3;

   public static final String HELPSET_DIR = "/resources/helpsets/";
   public static final String DICT_DIR = "/resources/dictionaries/";

   public static final String RESOURCE = "texparsertest";

   public static final Pattern PATTERN_HELPSET 
     = Pattern.compile("texparsertest-([a-z]{2})(-[A-Z]{2})?");

   public static final Pattern PATTERN_DICT 
     = Pattern.compile("texparsertest-([a-z]{2})(-[A-Z]{2})?\\.xml");
}
