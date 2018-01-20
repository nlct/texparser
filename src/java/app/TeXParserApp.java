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
import java.text.MessageFormat;
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
      guiMode = false;

      settings = new TeXParserAppSettings(this);

      initDefaultErrorListener(this);

      initProperties();

      currentProcessListeners = new Vector<ProcessListener>();
   }

   private void initDefaultErrorListener(final TeXParserApp app)
   {
      errorListener = new ErrorListener()
      {
         // check for GUI mode in case error occurs before GUI
         // system initialised.

         public void warning(String message)
         {
            if (guiMode)
            {
               TeXParserAppGuiResources.warning(app, message);
            }
            else
            {
               System.err.println(String.format("%s: %s", APP_NAME, message));
            }
         }

         public void error(String message)
         {
            if (guiMode)
            {
               TeXParserAppGuiResources.error(app, message);
            }
            else
            {
               System.err.println(String.format("%s: %s", APP_NAME, message));
            }
         }

         public void error(Exception e)
         {
            error(e.getMessage());
            e.printStackTrace();
         }
      };
   }

   private void initProperties()
   {
      try
      {
         settings.loadProperties();
      }
      catch (IOException e)
      {
         System.err.println(String.format("%s: unable to load properties:%s%n",
           APP_NAME, e.getMessage()));
      }

      try
      {
         loadDictionary();
      }
      catch (Exception e)
      {
         System.err.println(String.format(
           "%s: unable to load dictionary file:%s%n",
           APP_NAME, e.getMessage()));
      }
   }

   public TeXParserAppGUI getGui()
   {
      return guiApp;
   }

   private void doBatchProcess()
   {
      try
      {
         if (inFileName == null)
         {
            throw new InvalidSyntaxException(
               getMessage("error.syntax.batch.missing_in"));
         }

         if (outDir == null)
         {
            throw new InvalidSyntaxException(
               getMessage("error.syntax.batch.missing_out"));
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
               getMessage("error.syntax.batch.unknown_format", outputFormat));
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
         throw new IOException(getMessage(
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
         throw new IOException(getMessage(
            "error.exists", outDir.getAbsolutePath()));
      }

      L2HConverter listener = new L2HConverter(this, true, outDir, true);

      TeXParser parser = new TeXParser(listener);

      parser.parse(new File(inFileName));
   }

   public void progress(int percentage)
   {
   }

   public void message(String text)
   {
      System.out.println(text);
   }

   public String requestUserInput(String message)
     throws IOException
   {
      return javax.swing.JOptionPane.showInputDialog(null, message);
   }

   public void warning(TeXParser parser, String message)
   {
      System.err.println(APP_NAME+": "+message);
   }

   public String tag(String string)
   {
      return string;
   }

   public void substituting(TeXParser parser, String original, String replacement)
   {
      File file = parser.getCurrentFile();
      int lineNum = parser.getLineNumber();
      String message;

      if (replacement.isEmpty())
      {
         message = getMessage("warning.removing", tag(original));
      }
      else
      {
         message = getMessage("warning.substituting",
              tag(original), tag(replacement));
      }

      if (file == null)
      {
         errorListener.warning(message);
      }
      else if (lineNum > 0)
      {
         errorListener.warning(String.format("%s:%d: %s", file.getName(), 
           lineNum, message));
      }
      else
      {
         errorListener.warning(String.format("%s: %s", file.getName(), 
           message));
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
         throw new IOException(getMessage("error.app_failed",
           String.format("%s \"%s\"", app, fileName), exitCode));
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
         throw new IOException(getMessage("error.app_failed",
           String.format("%s \"%s\"", app, fileName), exitCode));
      }
   }

   public boolean isReadAccessAllowed(TeXPath path)
   {
      return isReadAccessAllowed(path.getFile());
   }

   public boolean isReadAccessAllowed(File file)
   {
      return file.canRead();
   }

   public boolean isWriteAccessAllowed(TeXPath path)
   {
      return isWriteAccessAllowed(path.getFile());
   }

   public boolean isWriteAccessAllowed(File file)
   {
      if (file.exists())
      {
         return file.canWrite();
      }

      File dir = file.getParentFile();

      if (dir != null)
      {
         return dir.canWrite();
      }

      return (new File(System.getProperty("user.dir"))).canWrite();
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
         interruptor = new InterruptTimerTask(this, Thread.currentThread());
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
         String message = getMessage("message.running", execName+params);

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

      ProcessInputReaderThread inReaderThread 
         = new ProcessInputReaderThread(this, p, listener);
      listener.setThread(inReaderThread);
      inReaderThread.start();
      inReaderThread = null;

      return p;
   }

//http://www.forward.com.au/javaProgramming/HowToStopAThread.html
   public synchronized void checkForInterrupt()
     throws InterruptedException
   {
      Thread.yield();

      if (Thread.currentThread().isInterrupted())
      {
         throw new CancelledException(this);
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

   public void help()
   {
      version();
      System.out.println();
      System.out.println(getMessage("syntax.title"));
      System.out.println();
      System.out.println(APP_NAME+" --gui");
      System.out.println(getMessage("syntax.or"));
      System.out.println(getMessage("syntax.opt_in", APP_NAME));
      System.out.println();
      System.out.println(getMessage("syntax.general"));
      System.out.println(getMessage("syntax.gui", "--gui", "-g"));
      System.out.println(getMessage("syntax.batch", "--batch", "-b"));
      System.out.println(getMessage("syntax.in", "--in", "-i", APP_NAME));
      System.out.println(getMessage("syntax.out", "--output", "-o"));
      System.out.println(getMessage("syntax.latex", "--latex"));
      System.out.println(getMessage("syntax.html", "--html"));
      System.out.println(getMessage("syntax.version", "--version", "-v"));
      System.out.println(getMessage("syntax.help", "--help", "-h"));
      System.out.println(getMessage("syntax.debug", "--debug"));
      System.out.println(getMessage("syntax.nodebug", "--nodebug"));
      System.out.println();
      System.out.println(getMessage("syntax.bugreport", 
        "https://github.com/nlct/texparser"));
      System.out.println(getMessage("syntax.homepage", 
        APP_NAME,
        "http://www.dickimaw-books.com/software/texparser/"));
   }

   public String getAppInfo()
   {
      String info = String.format("%s%n%s%n%s",
         getMessage("about.version", APP_NAME, APP_VERSION, APP_DATE),
// Copyright line shouldn't get translated (according to
// http://www.gnu.org/prep/standards/standards.html)
        "Copyright (C) 2013 Nicola L. C. Talbot (www.dickimaw-books.com)",
         getMessage("about.legal"));

      String translator = getLabelWithAlt("about.translator_info", null);

      if (translator != null && !translator.isEmpty())
      {
         info = String.format("%s%n%s", info, translator);
      }

      String ack = getLabelWithAlt("about.acknowledgements", null);

      if (ack != null && !ack.isEmpty())
      {
         info = String.format("%s%n%n%s", info, ack);
      }

      return info;
   }

   public void version()
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
      if (e instanceof TeXSyntaxException)
      {
         errorListener.error(((TeXSyntaxException)e).getMessage(this));

         if (debugMode)
         {
            e.printStackTrace();
         }
      }
      else
      {
         errorListener.error(e);
      }
   }

   public void debug(String message)
   {
      if (debugMode)
      {
         System.err.println(String.format("%s: %s", APP_NAME, message));
      }
   }

   public void debug(Exception e)
   {
      if (debugMode)
      {
         System.err.println(String.format("%s:", APP_NAME));
         e.printStackTrace();
      }
   }

   public void loadDictionary()
      throws IOException
   {
      String dictLanguage = settings.getDictionary();

      InputStream in = null;

      try
      {
         String dict = String.format("%s-%s.xml",
            settings.getDictionaryLocation(),
            dictLanguage);

         URL url = getClass().getResource(dict);

         if (url == null)
         {
            throw new FileNotFoundException
            (
               "Can't find dictionary resource file " +dict
            );
         }

         in = url.openStream();

         Properties dictionary = new Properties();
         dictionary.loadFromXML(in);

         messages = new TeXParserAppMessages(dictionary);
      }
      finally
      {
         if (in != null)
         {
            in.close();
         }
      }
   }

   public String getMessage(String label, Object... params)
   {
      if (messages == null)
      {// message system hasn't been initialised

         String param = (params.length == 0 ? "" : params[0].toString());

         for (int i = 1; i < params.length; i++)
         {
            param += ","+params[0].toString();
         }

         return String.format("%s[%s]", label, param);
      }

      String msg = messages.getMessageIfExists(label, params);

      if (msg == null)
      {
         warning("Can't find message for label: "+label);

         return label;
      }

      return msg;
   }

   public String getLabelWithAlt(String label, String alt)
   {
      if (messages == null) return alt;

      String msg = messages.getMessage(label);

      if (msg == null)
      {
         return alt;
      }

      return msg;
   }

   public String getLabelRemoveArgs(String parent, String label)
   {
      return getLabel(parent, label).replaceAll("\\{[0-9]\\}", "");
   }

   public String getLabel(String label)
   {
      return getLabel(null, label);
   }

   public String getLabel(String parent, String label)
   {
      if (parent != null)
      {
         label = parent+"."+label;
      }

      String msg = messages.getMessageIfExists(label);

      if (msg == null)
      {
         System.err.println(APP_NAME+": no such dictionary property '"+label+"'");
         return "?"+label+"?";
      }

      return msg;
   }

   public String getToolTip(String label)
   {
      return getToolTip(null, label);
   }

   public String getToolTip(String parent, String label)
   {
      if (parent != null)
      {
         label = parent+"."+label;
      }

      return getLabelWithAlt(label+".tooltip", null);
   }

   public char getMnemonic(String label)
   {
      return getMnemonic(null, label);
   }

   public char getMnemonic(String parent, String label)
   {
      return (char)getMnemonicInt(parent, label);
   }

   public int getMnemonicInt(String label)
   {
      return getMnemonicInt(null, label);
   }

   public int getMnemonicInt(String parent, String label)
   {
      String propName;

      if (parent == null)
      {
         propName = String.format("%s.mnemonic", label);
      }
      else
      {
         propName = String.format("%s.%s.mnemonic", parent, label);
      }

      String prop = getLabelWithAlt(propName, null);

      if (prop == null || prop.equals(""))
      {
         debug(String.format("missing dictionary property '%s'", prop));
         return -1;
      }

      return prop.codePointAt(0);
   }

   private void parseArgs(String[] args) throws InvalidSyntaxException
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
            if (outDir != null)
            {
               throw new InvalidSyntaxException(
                 getMessage("error.syntax.only_one", args[i]));
            }

            i++;

            if (i == args.length)
            {
               throw new InvalidSyntaxException(
                 getMessage("error.syntax.missing_filename", args[i-1]));
            }

            outDir = new File(args[i]);

         }
         else if (args[i].equals("--gui") || args[i].equals("-g"))
         {
            guiMode = true;
         }
         else if (args[i].equals("--batch") || args[i].equals("-b"))
         {
            guiMode = false;
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
            debugMode = true;
         }
         else if (args[i].equals("--nodebug"))
         {
            debugMode = false;
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
                 getMessage("error.syntax.missing_input",
                   args[i-1]));
            }

            if (inFileName != null)
            {
               throw new InvalidSyntaxException(
                 getMessage("error.syntax.only_one_input"));
            }

            inFileName = args[i];
         }
         else if (args[i].charAt(0) == '-')
         {
            throw new InvalidSyntaxException(
             getMessage("error.syntax.unknown_option", args[i]));
         }
         else
         {
            // if no option specified, assume --in

            if (inFileName != null)
            {
               throw new InvalidSyntaxException(
                 getMessage("error.syntax.only_one_input"));
            }

            inFileName = args[i];
         }
      }
   }

   private void runApplication()
   {
      if (guiMode)
      {
         javax.swing.SwingUtilities.invokeLater(new Runnable()
          {
             public void run()
             {
                createAndShowGUI();
             }
          });
      } 
      else
      {
         doBatchProcess();
      }
   }

   public static void main(String[] args)
   {
      final TeXParserApp app = new TeXParserApp();

      try
      {
         app.parseArgs(args);
      }
      catch (Exception e)
      {
         app.error(e);

         System.exit(1);
      }

      app.runApplication();
   }

   public static final String APP_VERSION = "0.3b.20180120";
   public static final String APP_NAME = "texparserapp";
   public static final String APP_DATE = "2018-01-20";

   public static long MAX_PROCESS_TIME=0L;

   private boolean debugMode = false;

   private boolean guiMode = false;

   private TeXParserAppSettings settings;

   private TeXParserAppGUI guiApp = null;

   private String inFileName;

   private File outDir;

   private Vector<ProcessListener> currentProcessListeners;

   private ErrorListener errorListener;

   private TeXParserAppMessages messages;

   private File texmf;

   private String outputFormat = "latex";
}
