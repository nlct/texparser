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

package com.dickimawbooks.texparserapp;

import java.util.Properties;
import java.util.Vector;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.plain.*;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex2latex.*;
import com.dickimawbooks.texparserlib.html.*;

import com.dickimawbooks.texparserapp.gui.*;
import com.dickimawbooks.texparserapp.io.*;

public class TeXParserApp implements TeXApp
{
   public TeXParserApp()
   {
      settings = new TeXParserAppSettings();

      guiMode = false;

      try
      {
         settings.loadProperties();
      }
      catch (IOException e)
      {
         System.err.println(appName+": unable to load properties:\n" +
           e.getMessage());
      }

      try
      {
         loadDictionary();
      }
      catch (IOException e)
      {
         System.err.println(appName+": unable to load dictionary file:\n"
           + e.getMessage());
      }

      currentProcessListeners = new Vector<ProcessListener>();
   }

   private void doBatchProcess(String outputFormat)
   {
      errorListener = new ErrorListener()
      {
         public void warning(String message)
         {
            System.err.println(appName+": "+message);
         }

         public void error(String message)
         {
            System.err.println(appName+": "+message);
         }

         public void error(Exception e)
         {
            System.err.println(appName+": "+e.getMessage());
            e.printStackTrace();
         }
      };


      try
      {
         if (inFileName == null)
         {
            throw new InvalidSyntaxException(
               getLabel("error.syntax.batch.missing_in"));
         }

         if (outDir == null)
         {
            throw new InvalidSyntaxException(
               getLabel("error.syntax.batch.missing_out"));
         }

         if (outputFormat.equals("latex"))
         {
            latex2latex(inFileName, outDir);
         }
         else if (outputFormat.equals("html"))
         {
            latex2html(inFileName, outDir);
         }
         else
         {
            throw new InvalidSyntaxException(
               getLabelWithValue("error.syntax.batch.unknown_format", outputFormat));
         }
      }
      catch (IOException e)
      {
         error(e);
      }
   }

   private void createAndShowGUI()
   {
      guiApp = new TeXParserAppGUI(this);
      errorListener = guiApp;
   }

   public void latex2latex(String inFileName, File outDir)
     throws IOException
   {
      this.inFileName = inFileName;
      this.outDir = outDir;

      if (outDir.exists())
      {
         throw new IOException(getLabelWithValue(
            "error.exists", outDir.getAbsolutePath()));
      }

      LaTeX2LaTeX listener = new LaTeX2LaTeX(this, outDir);

      TeXParser parser = new TeXParser(listener);

      parser.parse(new File(inFileName));
   }

   public void latex2html(String inFileName, File outDir)
     throws IOException
   {
      this.inFileName = inFileName;
      this.outDir = outDir;

      if (outDir.exists())
      {
         throw new IOException(getLabelWithValue(
            "error.exists", outDir.getAbsolutePath()));
      }

      L2HConverter listener = new L2HConverter(this, true, outDir, true);

      TeXParser parser = new TeXParser(listener);

      parser.parse(new File(inFileName));
   }

   public void message(int type, String arg)
   {
      switch (type)
      {
         case MESSAGE_READING:
            System.out.println(getLabelWithValue("message.reading", arg));
         return;
         case MESSAGE_WRITING:
            System.out.println(getLabelWithValue("message.writing", arg));
         return;
      }

      System.out.println(arg);
   }

   public String tag(String string)
   {
      return string;
   }

   public void substituting(int lineNum, String original, String replacement)
   {
      if (replacement.isEmpty())
      {
         errorListener.warning(getLabelWithValues("error.line", ""+lineNum, 
           getLabelWithValue("warning.removing", tag(original))));
      }
      else
      {
         errorListener.warning(getLabelWithValues("error.line", ""+lineNum, 
           getLabelWithValues("warning.substituting",
              tag(original), tag(replacement))));
      }
   }

   public TeXParserAppSettings getSettings()
   {
      return settings;
   }

   public String kpsewhich(String name)
     throws IOException,InterruptedException
   {
      return kpsewhich(name, "kpsewhich");
   }

   public String kpsewhich(String name, String app)
     throws IOException,InterruptedException
   {
      int exitCode = -1;

      KpsewhichListener listener = new KpsewhichListener(this);

      exitCode = execCommandAndWaitFor(new String[]{app, name},
            listener);

      if (exitCode != 0)
      {
         return null; // file not found
      }

      return listener.getFile().getAbsolutePath();
   }

   public File getTeXMF()
      throws IOException
   {
      if (texmf != null)
      {
         return texmf;
      }

      // Try to use kpsewhich -var-value=TEXMFHOME to find target
      // directory

      try
      {
         texmf = new File(kpsewhich("-var-value=TEXMFHOME"));
      }
      catch (InterruptedException e)
      {
         error(e);
      }

      return texmf;
   }

   public void epstopdf(File file, File pdfFile)
     throws IOException,InterruptedException
   {
      epstopdf(file, pdfFile, "epstopdf");
   }

   public void epstopdf(File file, File pdfFile, String app)
     throws IOException,InterruptedException
   {
      int exitCode = -1;

      String fileName = file.getAbsolutePath();

      exitCode = execCommandAndWaitFor(new String[]{app, 
         "--outfile="+pdfFile.getAbsolutePath(),
         fileName});

      if (exitCode != 0)
      {
         throw new IOException(getLabelWithValues("error.app_failed",
           app+" \""+fileName+"\"", ""+exitCode));
      }
   }

   public void wmftoeps(File file, File epsFile)
     throws IOException,InterruptedException
   {
      wmftoeps(file, epsFile, "wmf2eps");
   }

   public void wmftoeps(File file, File epsFile, String app)
     throws IOException,InterruptedException
   {
      int exitCode = -1;

      String fileName = file.getAbsolutePath();

      exitCode = execCommandAndWaitFor(new String[]{app, 
         "-o", epsFile.getAbsolutePath(),
         fileName});

      if (exitCode != 0)
      {
         throw new IOException(getLabelWithValues("error.app_failed",
           app+" \""+fileName+"\"", ""+exitCode));
      }
   }

   public void copyFile(File src, File dest)
   throws IOException
   {
      File destDirFile = dest.getParentFile();

      if (!destDirFile.exists())
      {
          Files.createDirectories(destDirFile.toPath());
      }

      Files.copy(src.toPath(), dest.toPath());
   }

   public void copyFile(File src, String destDir, String destName)
   throws IOException
   {
      File destDirFile = new File(destDir);

      if (!destDirFile.exists())
      {
          Files.createDirectories(destDirFile.toPath());
      }

      Files.copy(src.toPath(), (new File(destDirFile, destName)).toPath());
   }

   public void copyFile(String srcDir, String srcName, 
      String destDir, String destName)
   throws IOException
   {
      File destDirFile = new File(destDir);

      if (!destDirFile.exists())
      {
          Files.createDirectories(destDirFile.toPath());
      }

      Path source = FileSystems.getDefault().getPath(
         srcDir, srcName);
      Path target = FileSystems.getDefault().getPath(
         destDir, destName);

      Files.copy(source, target);
   }

   public long getMaxProcessTime()
   {
      if (MAX_PROCESS_TIME > 0L)
      {
         return MAX_PROCESS_TIME;
      }

      return settings.getMaxProcessTime();
   }

   public int execCommandAndWaitFor(String[] cmd)
     throws IOException,InterruptedException
   {
      return execCommandAndWaitFor(cmd, null, null, new DefaultProcessListener(this), true);
   }

   public int execCommandAndWaitFor(String[] cmd, boolean writeInfo)
     throws IOException,InterruptedException
   {
      return execCommandAndWaitFor(cmd, null, null, new DefaultProcessListener(this), writeInfo);
   }

   public int execCommandAndWaitFor(String[] cmd, ProcessListener listener)
     throws IOException,InterruptedException
   {
      return execCommandAndWaitFor(cmd, null, null, listener, true);
   }

   public int execCommandAndWaitFor(String[] cmd, String[] envp, File dir)
     throws IOException,InterruptedException
   {
      return execCommandAndWaitFor(cmd, envp, dir, new DefaultProcessListener(this), true);
   }

   public int execCommandAndWaitFor(String[] cmd, String[] envp, File dir, ProcessListener listener)
     throws IOException,InterruptedException
   {
      return execCommandAndWaitFor(cmd, envp, dir, listener, true);
   }

   // adapted from http://kylecartmell.com/?p=9
   public int execCommandAndWaitFor(String[] cmd, String[] envp, File dir, 
      ProcessListener listener, boolean writeInfo)
     throws IOException,InterruptedException
   {
      java.util.Timer timer = null;
      Process p = null;
      int exitCode = -1;

      InterruptTimerTask interruptor = null;

      try
      {
         timer = new java.util.Timer(true);
         interruptor = new InterruptTimerTask(Thread.currentThread());
         timer.schedule(interruptor, getMaxProcessTime());
         listener.setInterruptor(interruptor);

         p = execCommand(cmd, envp, dir, listener, writeInfo);

         addProcessListener(listener);

         exitCode = p.waitFor();
      }
      catch (InterruptedException e)
      {
         p.destroy();

         throw e;
      }
      finally
      {
         timer.cancel();
         Thread.interrupted();
         removeProcessListener(listener);

         if (guiApp != null)
         {
            guiApp.updateAbortItem();
         }
      }

      return exitCode;
   }

   public Process execCommand(String[] cmd)
     throws IOException
   {
      return execCommand(cmd, null, null, new DefaultProcessListener(this), true);
   }

   public Process execCommand(String[] cmd, boolean writeInfo)
     throws IOException
   {
      return execCommand(cmd, null, null, new DefaultProcessListener(this), writeInfo);
   }

   public Process execCommand(String[] cmd, ProcessListener listener)
     throws IOException
   {
      return execCommand(cmd, null, null, listener, true);
   }

   public Process execCommand(String[] cmd, String[] envp, File dir)
     throws IOException
   {
      return execCommand(cmd, envp, dir, new DefaultProcessListener(this), true);
   }

   public Process execCommand(String[] cmd, String[] envp, File dir, ProcessListener listener)
     throws IOException
   {
      return execCommand(cmd, envp, dir, listener, true);
   }

   public Process execCommand(String[] cmd, String[] envp, File dir,
       ProcessListener listener, boolean writeInfo)
     throws IOException
   {
      String params = "";

      for (int i = 1; i < cmd.length; i++)
      {
         params += " \""+cmd[i]+"\"";
      }

      String execName = cmd[0];

      int idx = execName.lastIndexOf(File.separator);

      if (idx > -1)
      {
         execName = execName.substring(idx+1);
      }

      if (debugMode)
      {
         System.out.println("Running:");

         if (dir != null)
         {
            System.out.println("  in directory "+dir.getAbsolutePath());
         }

         if (envp != null && envp.length > 0)
         {
            System.out.println("  with environment:");

            for (int i = 0; i < envp.length; i++)
            {
               System.out.println("  "+envp[i]);
            }
         }

         System.out.println(cmd[0]+params);
      }

      if (writeInfo)
      {
         String message = getLabelWithValue("message.running", execName+params);

         if (guiApp != null)
         {
            guiApp.setInfo(message);
         }
         else
         {
            System.out.println(message);
         }
      }

      Process p = Runtime.getRuntime().exec(cmd, envp, dir);

      listener.setProcess(p);

      ProcessInputReaderThread inReaderThread = new ProcessInputReaderThread(p, listener);
      listener.setThread(inReaderThread);
      inReaderThread.start();
      inReaderThread = null;

      return p;
   }

//http://www.forward.com.au/javaProgramming/HowToStopAThread.html
   public static synchronized void checkForInterrupt()
     throws InterruptedException
   {
      Thread.yield();

      if (Thread.currentThread().isInterrupted())
      {
         throw new CancelledException();
      }
   }

   public synchronized void addProcessListener(ProcessListener listener)
   {
      if (currentProcessListeners != null)
      {
         currentProcessListeners.add(listener);
      }
   }

   public synchronized void removeProcessListener(ProcessListener listener)
   {
      if (currentProcessListeners != null)
      {
         currentProcessListeners.remove(listener);
      }
   }

   public synchronized boolean hasProcessesRunning()
   {
      return (currentProcessListeners != null
           && currentProcessListeners.size() > 0);
   }

   public static String getErrorMessage(LaTeXSyntaxException e)
   {
      String message;

      switch (e.getErrorCode())
      {
         case LaTeXSyntaxException.ERROR_MULTI_BEGIN_DOC:
            message = getLabel("error.multi_begin_doc");
         break;
         case LaTeXSyntaxException.ERROR_NO_BEGIN_DOC:
            message = getLabel("error.no_begin_doc");
         break;
         case LaTeXSyntaxException.ERROR_MULTI_CLS:
            message = getLabel("error.multi_cls");
         break;
         case LaTeXSyntaxException.ERROR_MISSING_KEY:
            message = getLabelWithValue("error.missing_key", e.getParam());
         break;
         case LaTeXSyntaxException.ERROR_EXTRA_END:
            message = getLabelWithValue("error.extra_end", e.getParam());
         break;
         case LaTeXSyntaxException.ERROR_UNACCESSIBLE:
            message = getLabelWithValue("error.unaccessible", e.getParam());
         break;
         case LaTeXSyntaxException.ERROR_DEFINED:
            message = getLabelWithValue("error.defined", e.getParam());
         break;
         default:
            message = e.getMessage();
      }

      int lineNum = e.getLineNumber();

      return (lineNum == -1 ? message : 
         getLabelWithValues("error.line", ""+lineNum, message));
   }

   public static String getErrorMessage(TeXSyntaxException e)
   {
      String message;

      switch (e.getErrorCode())
      {
         case TeXSyntaxException.ERROR_BAD_PARAM:
           message = getLabelWithValue("error.bad_param", e.getParam());
         break;
         case TeXSyntaxException.ERROR_NO_EG:
           message = getLabel("error.no_eg");
         break;
         case TeXSyntaxException.ERROR_PAR_BEFORE_EG:
           message = getLabel("error.par_before_eg");
         break;
         case TeXSyntaxException.ERROR_UNEXPECTED_EG:
           message = getLabel("error.unexpected_eg");
         break;
         case TeXSyntaxException.ERROR_MISSING_ENDMATH:
           message = getLabel("error.missing_endmath");
         break;
         case TeXSyntaxException.ERROR_DOLLAR2_ENDED_WITH_DOLLAR:
           message = getLabel("error.dollar2_ended_with_dollar");
         break;
         case TeXSyntaxException.ERROR_NOT_FOUND:
           message = getLabelWithValue("error.not_found", e.getParam());
         break;
         case TeXSyntaxException.ERROR_MISSING_PARAM:
           message = getLabel("error.missing_param");
         break;
         case TeXSyntaxException.ERROR_NOT_MATH_MODE:
           message = getLabelWithValue("error.not_math_mode", e.getParam());
         break;
         case TeXSyntaxException.ERROR_INVALID_ACCENT:
           message = getLabelWithValue("error.invalid_accent", e.getParam());
         break;
         case TeXSyntaxException.ERROR_AMBIGUOUS_MIDCS:
           message = getLabelWithValue("error.ambiguous_midcs", e.getParam());
         break;
         case TeXSyntaxException.ERROR_MISSING_CLOSING:
           message = getLabelWithValue("error.missing_closing", e.getParam());
         break;
         case TeXSyntaxException.ERROR_DIMEN_EXPECTED:
           message = getLabel("error.dimen_expected");
         break;
         case TeXSyntaxException.ERROR_MISSING_UNIT:
           message = getLabel("error.missing_unit");
         break;
         case TeXSyntaxException.ERROR_EXPECTED:
           message = getLabelWithValue("error.expected", e.getParam());
         break;
         case TeXSyntaxException.ERROR_UNDEFINED:
           message = getLabelWithValue("error.undefined", e.getParam());
         break;
         case TeXSyntaxException.ERROR_CS_EXPECTED:
           message = getLabelWithValue("error.cs_expected", e.getParam());
         break;
         case TeXSyntaxException.ERROR_NUMBER_EXPECTED:
           message = getLabelWithValue("error.number_expected", e.getParam());
         break;
         case TeXSyntaxException.ERROR_REGISTER_UNDEF:
           message = getLabelWithValue("error.register_undef", e.getParam());
         break;
         case TeXSyntaxException.ERROR_SYNTAX:
           message = getLabelWithValue("error.syntax", e.getParam());
         break;
         case TeXSyntaxException.ERROR_EXTRA:
           message = getLabelWithValue("error.extra", e.getParam());
         break;
         default:
           message = e.getMessage();
      }

      int lineNum = e.getLineNumber();

      return (lineNum == -1 ? message : 
         getLabelWithValues("error.line", ""+lineNum, message));
   }

   public static void help()
   {
      version();
      System.out.println();
      System.out.println(getLabel("syntax.title"));
      System.out.println();
      System.out.println(appName+" --gui");
      System.out.println(getLabel("syntax.or"));
      System.out.println(getLabelWithValue("syntax.opt_in", appName));
      System.out.println();
      System.out.println(getLabel("syntax.general"));
      System.out.println(getLabelWithValues("syntax.gui", "--gui", "-g"));
      System.out.println(getLabelWithValues("syntax.batch", "--batch", "-b"));
      System.out.println(getLabelWithValues("syntax.in", 
        new String[]{"--in", "-i", appName}));
      System.out.println(getLabelWithValues("syntax.out", "--output", "-o"));
      System.out.println(getLabelWithValue("syntax.latex", "--latex"));
      System.out.println(getLabelWithValue("syntax.html", "--html"));
      System.out.println(getLabelWithValues("syntax.version", "--version", "-v"));
      System.out.println(getLabelWithValues("syntax.help", "--help", "-h"));
      System.out.println(getLabelWithValue("syntax.debug", "--debug"));
      System.out.println(getLabelWithValue("syntax.nodebug", "--nodebug"));
      System.out.println();
      System.out.println(getLabelWithValue("syntax.bugreport", 
        "http://www.dickimaw-books.com/bug-report.html"));
      System.out.println(getLabelWithValues("syntax.homepage", 
        appName,
        "http://www.dickimaw-books.com/apps/texparser/"));
   }

   public static String getAppInfo()
   {
      String eol = System.getProperty("line.separator", "\n");

      String info = getLabelWithValues("about.version",
        new String[]{ appName, appVersion, appDate})
        + eol
// Copyright line shouldn't get translated (according to
// http://www.gnu.org/prep/standards/standards.html)
        + "Copyright (C) 2013 Nicola L. C. Talbot (www.dickimaw-books.com)"
        + eol
        + getLabel("about.legal");

      String translator = dictionary.getProperty("about.translator_info");

      if (translator != null && !translator.isEmpty())
      {
         info += eol + translator;
      }

      String ack = dictionary.getProperty("about.acknowledgements");

      if (ack != null && !ack.isEmpty())
      {
         ack += eol + eol + ack;
      }

      return info;
   }

   public static void version()
   {
      System.out.println(getAppInfo());
   }

   public void warning(String message)
   {
      errorListener.warning(message);
   }

   public void error(String message)
   {
      errorListener.error(message);
   }

   public void error(Exception e)
   {
      if (e instanceof LaTeXSyntaxException)
      {
         errorListener.error(getErrorMessage((LaTeXSyntaxException)e));
      }
      else if (e instanceof TeXSyntaxException)
      {
         errorListener.error(getErrorMessage((TeXSyntaxException)e));
      }
      else
      {
         errorListener.error(e);
      }
   }

   public static void debug(String message)
   {
      if (debugMode)
      {
         System.err.println(appName+": "+message);
      }
   }

   public static void debug(Exception e)
   {
      if (debugMode)
      {
         System.err.println(appName+":");
         e.printStackTrace();
      }
   }

   public static String getDictionary()
   {
      return dict;
   }

   public static URL getDictionaryUrl()
   {
      return TeXParserApp.class.getResource(dict);
   }

   public void loadDictionary()
      throws IOException
   {
      String dictLanguage = settings.getDictionary();

      InputStream in = null;
      BufferedReader reader = null;

      try
      {
         dict = settings.getDictionaryLocation()+"-"
             + settings.getDictionary()+".prop";

         in = TeXParserApp.class.getResourceAsStream(dict);

         if (in == null)
         {
            throw new FileNotFoundException
            (
               "Can't find dictionary resource file " +dict
            );
         }

         reader = new BufferedReader(new InputStreamReader(in));

         dictionary = new Properties();
         dictionary.load(reader);
      }
      finally
      {
         if (reader != null)
         {
            reader.close();
         }

         if (in != null)
         {
            in.close();
         }
      }
   }

   public static String getLabelWithAlt(String label, String alt)
   {
      if (dictionary == null) return alt;

      String prop = dictionary.getProperty(label);

      if (prop == null)
      {
         return alt;
      }

      return prop;
   }

   public static String getLabelRemoveArgs(String parent, String label)
   {
      return getLabel(parent, label).replaceAll("\\$[0-9]", "");
   }

   public static String getLabel(String label)
   {
      return getLabel(null, label);
   }

   public static String getLabel(String parent, String label)
   {
      if (parent != null)
      {
         label = parent+"."+label;
      }

      String prop = dictionary.getProperty(label);

      if (prop == null)
      {
         System.err.println(appName+": no such dictionary property '"+label+"'");
         return "?"+label+"?";
      }

      return prop;
   }

   public static String getToolTip(String label)
   {
      return getToolTip(null, label);
   }

   public static String getToolTip(String parent, String label)
   {
      if (parent != null)
      {
         label = parent+"."+label;
      }

      return dictionary.getProperty(label+".tooltip");
   }

   public static char getMnemonic(String label)
   {
      return getMnemonic(null, label);
   }

   public static char getMnemonic(String parent, String label)
   {
      String prop = getLabel(parent, label+".mnemonic");

      if (prop.equals(""))
      {
         debug("empty dictionary property '"+prop+"'");
         return label.charAt(0);
      }

      return prop.charAt(0);
   }

   public static int getMnemonicInt(String label)
   {
      return getMnemonicInt(null, label);
   }

   public static int getMnemonicInt(String parent, String label)
   {
      String prop;

      if (parent == null)
      {
         prop = dictionary.getProperty(label+".mnemonic");
      }
      else
      {
         prop = dictionary.getProperty(parent+"."+label+".mnemonic");
      }

      if (prop == null || prop.isEmpty())
      {
         return -1;
      }

      return prop.codePointAt(0);
   }

   public static String getLabelWithValue(String label, String value)
   {
      String prop = getLabel(label);

      if (prop == null)
      {
         return null;
      }

      if (value == null)
      {
         value = "";
      }

      int n = prop.length();

      StringBuffer buffer = new StringBuffer(n);

      for (int i = 0; i < n; i++)
      {
         int c = prop.codePointAt(i);

         if (c == (int)'\\' && i != n-1)
         {
            buffer.appendCodePoint(prop.codePointAt(++i));
         }
         else if (c == (int)'$' && i != n-1)
         {
            c = prop.codePointAt(i+1);

            if (c == (int)'1')
            {
               buffer.append(value);
               i++;
            }
         }
         else
         {
            buffer.appendCodePoint(c);
         }
      }

      return new String(buffer);
   }

   public static String getLabelWithValue(String label, int value)
   {
      return getLabelWithValue(label, ""+value);
   }

   public static String getLabelWithValues(String label, int value1,
      String value2)
   {
      return getLabelWithValues(label, new String[] {""+value1, value2});
   }

   public static String getLabelWithValues(String label, String value1,
      String value2)
   {
      return getLabelWithValues(label, new String[] {value1, value2});
   }

   // Only works for up to nine values.

   public static String getLabelWithValues(String label, String[] values)
   {
      String prop = getLabel(label);

      if (prop == null)
      {
         return prop;
      }

      int n = prop.length();

      StringBuffer buffer = new StringBuffer(n);

      for (int i = 0; i < n; i++)
      {
         int c = prop.codePointAt(i);

         if (c == (int)'\\' && i != n-1)
         {
            buffer.appendCodePoint(prop.codePointAt(++i));
         }
         else if (c == (int)'$' && i != n-1)
         {
            c = prop.codePointAt(i+1);

            if (c >= 48 && c <= 57)
            {
               // Digit

               int index = c - 48 - 1;

               if (index >= 0 && index < values.length)
               {
                  buffer.append(values[index]);
               }

               i++;
            }
            else
            {
               buffer.append('$');
            }
         }
         else
         {
            buffer.appendCodePoint(c);
         }
      }

      return new String(buffer);
   }

   public static void main(String[] args)
   {
      final TeXParserApp app = new TeXParserApp();
      String outputFormat = "latex";

      try
      {
         for (int i = 0; i < args.length; i++)
         {
            if (args[i].equals("--version") || args[i].equals("-v"))
            {
               version();
               System.exit(0);
            }
            else if (args[i].equals("--help") || args[i].equals("-h"))
            {
               help();
               System.exit(0);
            }
            else if (args[i].equals("--output") || args[i].equals("-o"))
            {
               if (app.outDir != null)
               {
                  throw new InvalidSyntaxException(
                    getLabelWithValue("error.syntax.only_one", args[i]));
               }

               i++;

               if (i == args.length)
               {
                  throw new InvalidSyntaxException(
                    getLabelWithValue("error.syntax.missing_filename",
                      args[i-1]));
               }

               app.outDir = new File(args[i]);

            }
            else if (args[i].equals("--gui") || args[i].equals("-g"))
            {
               app.guiMode = true;
            }
            else if (args[i].equals("--batch") || args[i].equals("-b"))
            {
               app.guiMode = false;
            }
            else if (args[i].equals("--latex"))
            {
               outputFormat = "latex";
            }
            else if (args[i].equals("--html"))
            {
               outputFormat = "html";
            }
            else if (args[i].equals("--debug"))
            {
               app.debugMode = true;
            }
            else if (args[i].equals("--nodebug"))
            {
               app.debugMode = false;
            }
            else if (args[i].equals("-timeout"))
            {
               i++;

               if (i == args.length)
               {
                  throw new IllegalArgumentException("-timeout requires numerical argument.");
               }
   
               try
               {
                  MAX_PROCESS_TIME = Long.parseLong(args[i]);
               }
               catch (NumberFormatException e)
               {
                  throw new IllegalArgumentException(
                     "-timeout requires numerical argument. Found: '"+args[i]+"'",
                     e);
               }
            }
            else if (args[i].equals("--in") || args[i].equals("-i"))
            {
               i++;

               if (i == args.length)
               {
                  throw new InvalidSyntaxException(
                    getLabelWithValue("error.syntax.missing_input",
                      args[i-1]));
               }

               if (app.inFileName != null)
               {
                  throw new InvalidSyntaxException(
                    getLabel("error.syntax.only_one_input"));
               }

               app.inFileName = args[i];
            }
            else if (args[i].charAt(0) == '-')
            {
               throw new InvalidSyntaxException(
                getLabelWithValue("error.syntax.unknown_option",
                  args[i]));
            }
            else
            {
               // if no option specified, assume --in

               if (app.inFileName != null)
               {
                  throw new InvalidSyntaxException(
                    getLabel("error.syntax.only_one_input"));
               }

               app.inFileName = args[i];
            }
         }
      }
      catch (Exception e)
      {
         if (app.guiMode)
         {
            TeXParserAppGuiResources.error(null, e);
         }
         else
         {
            System.err.println(appName+": "+
              getLabelWithValue("error.syntax", e.getMessage()));
         }

         System.exit(1);
      }

      if (app.guiMode)
      {
         javax.swing.SwingUtilities.invokeLater(new Runnable()
          {
             public void run()
             {
                app.createAndShowGUI();
             }
          });
      } 
      else
      {
         app.doBatchProcess(outputFormat);
      }
   }

   public static final String appVersion = "0.1b";
   public static final String appName = "texparserapp";
   public static final String appDate = "2013-09-16";

   private static Properties dictionary;

   private static String dict = null;

   public static long MAX_PROCESS_TIME=0L;

   private static boolean debugMode = false;

   private boolean guiMode = false;

   private TeXParserAppSettings settings;

   private TeXParserAppGUI guiApp = null;

   private String inFileName;

   private File outDir;

   private Vector<ProcessListener> currentProcessListeners;

   private ErrorListener errorListener;

   private File texmf;
}
