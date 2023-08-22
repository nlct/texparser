/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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
import java.util.Vector;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.MessageFormat;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.charset.Charset;
import java.awt.Dimension;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.plain.*;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex2latex.*;
import com.dickimawbooks.texparserlib.html.*;

import com.dickimawbooks.texparsertest.io.*;

public class TeXParserApp implements TeXApp
{
   public TeXParserApp() 
   {

      settings = new TeXParserAppSettings(this);

      initDefaultErrorListener(this);

      initProperties();

      currentProcessListeners = new Vector<ProcessListener>();
   }

   private void initDefaultErrorListener(final TeXParserApp app)
   {
      errorListener = new ErrorListener()
      {
         public void warning(String message)
         {
            System.err.println(String.format("%s: %s", APP_NAME, message));
         }

         public void error(String message)
         {
            System.err.println(String.format("%s: %s", APP_NAME, message));
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

   private void doBatchProcess() throws IOException
   {
      if (inFileName == null)
      {
         throw new InvalidSyntaxException(
            getMessage("error.syntax.batch.missing_in", "-h"));
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

   public void latex2latex(File inFileName, File outDir)
     throws IOException
   {
      this.inFileName = inFileName;
      this.outDir = outDir;

      if (outDir.exists())
      {
         throw new IOException(getMessage(
            "error.exists", outDir.getAbsolutePath()));
      }

      LaTeX2LaTeX listener = new LaTeX2LaTeX(this, outDir, outCharset);

      TeXParser parser = new TeXParser(listener);

      PrintWriter logWriter = null;

      if (logFile != null)
      {
         logWriter = new PrintWriter(logFile);
         parser.setDebugMode(debugMode, logWriter);
      }

      try
      {
         parser.parse(inFileName);
      }
      finally
      {
         if (logWriter != null)
         {
            logWriter.close();
         }
      }
   }

   public void latex2html(File inFileName, File outDir)
     throws IOException
   {
      this.inFileName = inFileName;
      this.outDir = outDir;

      if (outDir.exists())
      {
         throw new IOException(getMessage(
            "error.exists", outDir.getAbsolutePath()));
      }

      L2HConverter listener = new L2HConverter(this, mathJax, outDir, null, true,
        outCharset, true)
      {
         public L2HImage toImage(String preamble, 
          String content, String mimeType, TeXObject alt, String name, 
          boolean crop)
         throws IOException
         {
            if (convertImages)
            {
               try
               {
                  return createImage(getParser(), preamble, content, mimeType, alt, 
                    name, crop);
               }
               catch (InterruptedException e)
               {
                  throw new TeXSyntaxException(e, parser, 
                    getMessage("error.interrupted"));
               }
            }
            else
            {
               return null;
            }
         }

         public Dimension getImageSize(File file, String mimetype)
         {
            try
            {
               return getImageFileDimensions(getParser(), file, mimetype);
            }
            catch (IOException | InterruptedException e)
            {
               return null;
            }
         }
      };

      listener.setUseEntities(useHtmlEntities);

      if (extraHead != null)
      {
         listener.addToHead(extraHead);
      }

      TeXParser parser = new TeXParser(listener);

      PrintWriter logWriter = null;

      if (logFile != null)
      {
         logWriter = new PrintWriter(logFile);
         parser.setDebugMode(debugMode, logWriter);
      }

      try
      {
         parser.parse(inFileName);
      }
      finally
      {
         if (deleteTempDirOnExit)
         {
            deleteTempDir();
         }

         if (logWriter != null)
         {
            logWriter.close();
         }
      }
   }

   private void deleteTempDir() throws IOException
   {
      if (tmpDir == null) return;

      File[] files = tmpDir.listFiles();

      for (File f : files)
      {
         f.delete();
      }

      tmpDir.delete();
   }

   public L2HImage createImage(TeXParser parser, String preamble, 
    String content, String mimetype, TeXObject alt, String name,
    boolean crop)
   throws IOException,InterruptedException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      if (name == null)
      {
         nameIdx++;
         name = String.format("img%06d", nameIdx);
      }

      Charset charset = listener.getCharSet();
      L2HImage image = null;
      PrintWriter writer = null;

      try
      {
         if (tmpDir == null)
         {
            tmpDir = Files.createTempDirectory("texparserlib").toFile();
         }

         File file = new File(tmpDir, name+".tex");

         if (charset == null)
         {
            writer = new PrintWriter(file);
         }
         else
         {
            writer = new PrintWriter(file, charset.name());
         }

         writer.println("\\batchmode");
         writer.println(preamble);
         writer.println("\\begin{document}");
         writer.println(content);
         writer.println("\\end{document}");

         writer.close();
         writer = null;

         String invoker;

         if (listener.isStyLoaded("fontspec"))
         {
            invoker = "lualatex";
         }
         else
         {
            invoker = "pdflatex";
         }

         File dir = inFileName.getParentFile();

         if (dir == null)
         {
            dir = new File(".");
         }

         int exitCode = execCommandAndWaitFor(
            new String[]{invoker, name}, 
            new String[]{String.format("TEXINPUTS=%s%c", 
              dir.getAbsolutePath(),
              File.pathSeparatorChar)}, tmpDir);

         if (exitCode != 0)
         {
            throw new IOException(getMessage("error.app_failed",
              String.format("%s \"%s\"", invoker, name), exitCode));
         }

         if (mimetype == null)
         {
            mimetype = L2HConverter.MIME_TYPE_PNG;
         }

         File pdfFile = new File(tmpDir, name+".pdf");
         Path destPath;

         if (crop)
         {
            invoker = "pdfcrop";

            String croppedPdfName = name+"-crop.pdf";

            exitCode = execCommandAndWaitFor(
               new String[]{invoker, pdfFile.getName(), croppedPdfName},
               null, tmpDir);

            if (exitCode == 0)
            {
               pdfFile = new File(tmpDir, croppedPdfName);
            }
            else
            {
               warning(parser, getMessage("error.app_failed",
                 String.format("%s \"%s\"", invoker, 
                    pdfFile.getName(), croppedPdfName),
                 exitCode));
            }
         }

         Dimension imageDim = null;

         if (mimetype.equals(L2HConverter.MIME_TYPE_PDF))
         {
            destPath = (new File(outDir, name+".pdf")).toPath();

            Files.copy(pdfFile.toPath(), destPath);
         }
         else if (mimetype.equals(L2HConverter.MIME_TYPE_PNG))
         {
            File pngFile = pdfToImage(pdfFile, name, "png");

            imageDim = getImageFileDimensions(parser, pngFile, mimetype);

            destPath = (new File(outDir, pngFile.getName())).toPath();

            Files.copy(pngFile.toPath(), destPath);
         }
         else if (mimetype.equals(L2HConverter.MIME_TYPE_JPEG))
         {
            File jpegFile = pdfToImage(pdfFile, name, "jpeg");

            imageDim = getImageFileDimensions(parser, jpegFile, mimetype);

            destPath = (new File(outDir, jpegFile.getName())).toPath();

            Files.copy(jpegFile.toPath(), destPath);
         }
         else
         {
            warning(parser, getMessage("warning.unsupported.image.type",
             mimetype));

            mimetype=L2HConverter.MIME_TYPE_PDF;
            destPath = (new File(outDir, name+".pdf")).toPath();
            Files.copy(pdfFile.toPath(), destPath);
         }

         int width=0;
         int height=0;

         if (imageDim != null)
         {
            width = imageDim.width;
            height = imageDim.height;
         }

         image = new L2HImage(outDir.toPath().relativize(destPath), 
          mimetype, width, height, name, alt);
      }
      finally
      {
         if (writer != null)
         {
            writer.close();
         }
      }

      return image;
   }

   public Dimension getImageFileDimensions(TeXParser parser, File file, 
     String type)
     throws IOException,InterruptedException
   {
      DefaultProcessListener processListener;

      String invoker = "file";

      boolean isPdf = L2HConverter.MIME_TYPE_PDF.equals(type);

      if (isPdf)
      {
         invoker = "pdfinfo";
         processListener = new DefaultProcessListener(this, true);
      }
      else
      {
         processListener = new DefaultProcessListener(this, 1);
      }

      int exitCode = execCommandAndWaitFor(
         new String[]{invoker, file.getAbsolutePath()}, null, null, processListener);

      Pattern pat = null;

      if (L2HConverter.MIME_TYPE_PNG.equals(type))
      {
         pat = PNG_INFO;
      }
      else if (L2HConverter.MIME_TYPE_JPEG.equals(type))
      {
         pat = JPEG_INFO;
      }
      else if (isPdf)
      {
         pat = PDF_INFO;
      }
      else
      {
         return null;
      }
         
      if (exitCode == 0)
      {
         String line = processListener.getContent();

         if (line == null)
         {
            return null;
         }

         Matcher m = pat.matcher(line);

         if (m.matches())
         {
            try
            {
               int width, height;

               if (isPdf)
               {
                  width = (int)Math.round(Float.parseFloat(m.group(1)));
                  height = (int)Math.round(Float.parseFloat(m.group(2)));
               }
               else
               {
                  width = Integer.parseInt(m.group(1));
                  height = Integer.parseInt(m.group(2));
               }

               return new Dimension(width, height);
            }
            catch (NumberFormatException e)
            {// shouldn't happen, pattern ensures format correct
               debug(e);
            }
         }
      }
      else
      {
         warning(parser, getMessage("error.app_failed",
           String.format("%s \"%s\"", invoker, file.getName()),
           exitCode));
      }

      return null;
   }

   protected File pdfToImage(File pdfFile, String basename, String format) 
     throws IOException,InterruptedException
   {
      String invoker = "pdftoppm";

      int exitCode = execCommandAndWaitFor(
          new String[]{invoker, "-singlefile", "-"+format, 
               pdfFile.getAbsolutePath(), basename}, 
            null, tmpDir);

      if (exitCode != 0)
      {
         throw new IOException(getMessage("error.app_failed",
           String.format("%s -singlefile -png \"%s\" \"%s\"", invoker, 
             pdfFile.getAbsolutePath(), basename), exitCode));
      }

      return new File(tmpDir, basename+"."+format);
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
      File file = parser.getCurrentFile();
      int lineNum = parser.getLineNumber();

      if (file != null && lineNum > 0)
      {
         System.err.format("%s:%d: %s%n", file.getName(), lineNum, message);
      }
      else
      {
         System.err.println(message);
      }
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
      if (name.indexOf("\\") != -1)
      {
         throw new IOException(getMessage("error.bksl_in_kpsewhich", name));
      }

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
         return null; // not found
      }

      return listener.getResult();
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

      if (!isReadAccessAllowed(file))
      {
         throw new IOException(getMessage("message.no.read", file));
      }

      if (!isWriteAccessAllowed(pdfFile))
      {
         throw new IOException(getMessage("message.no.write", pdfFile));
      }

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

      if (!isReadAccessAllowed(file))
      {
         throw new IOException(getMessage("message.no.read", file));
      }

      if (!isWriteAccessAllowed(epsFile))
      {
         throw new IOException(getMessage("message.no.write", epsFile));
      }

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

   public void convertimage(int inPage, String[] inOptions, File inFile,
     String[] outOptions, File outFile)
     throws IOException,InterruptedException
   {
      convertimage(inPage, inOptions, inFile, outOptions, outFile, "convert");
   }

   public void convertimage(int inPage, String[] inOptions, File inFile,
     String[] outOptions, File outFile, String app)
     throws IOException,InterruptedException
   {
      int exitCode = -1;

      if (!isReadAccessAllowed(inFile))
      {
         throw new IOException(getMessage("message.no.read", inFile));
      }

      if (!isWriteAccessAllowed(outFile))
      {
         throw new IOException(getMessage("message.no.write", outFile));
      }

      int numInOpts = (inOptions == null ? 0 : inOptions.length);
      int numOutOpts = (outOptions == null ? 0 : outOptions.length);

      String[] args = new String[3+numInOpts+numOutOpts];

      int idx = 0;
      args[idx++] = app;

      for (int i = 0; i < numInOpts; i++)
      {
         args[idx++] = inOptions[i];
      }

      if (inPage > 0)
      {
         args[idx++] = String.format("%s[%d]", inFile.getAbsolutePath(), inPage-1);
      }
      else
      {
         args[idx++] = inFile.getAbsolutePath();
      }

      for (int i = 0; i < numOutOpts; i++)
      {
         args[idx++] = outOptions[i];
      }

      args[idx++] = outFile.getAbsolutePath();

      exitCode = execCommandAndWaitFor(args);

      if (exitCode != 0)
      {
         throw new IOException(getMessage("error.app_failed",
           String.format("%s \"%s\" \"%s\"", app, inFile, outFile), exitCode));
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
      if (!isReadAccessAllowed(src))
      {
         throw new IOException(getMessage("message.no.read", src));
      }

      File destDirFile = dest.getParentFile();

      if (!destDirFile.exists())
      {
         if (!isWriteAccessAllowed(destDirFile))
         {
            throw new IOException(getMessage("message.no.write", destDirFile));
         }

          debug(String.format("mkdir %s", destDirFile));
          Files.createDirectories(destDirFile.toPath());
      }

      if (!isWriteAccessAllowed(dest))
      {
         throw new IOException(getMessage("message.no.write", dest));
      }

      debug(String.format("%s -> %s", src, dest));
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

      if (debugMode > 0)
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

         System.out.println(message);
      }

      ProcessBuilder pb = new ProcessBuilder(cmd);

      if (dir != null)
      {
         pb.directory(dir);
      }

      if (envp != null)
      {
         Map<String, String> env = pb.environment();

         for (String pair : envp)
         {
            String[] split = pair.split("=", 2);
            env.put(split[0], split[1]);
         }
      }

      Process p = pb.start();

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
      System.out.println(getMessage("syntax.opt_in", APP_NAME));
      System.out.println();
      System.out.println(getMessage("syntax.general"));
      System.out.println(getMessage("syntax.in", "--in", "-i", APP_NAME));
      System.out.println();
      System.out.println(getMessage("syntax.timeout", "--timeout"));
      System.out.println(getMessage("syntax.debug", "--debug"));
      System.out.println(getMessage("syntax.nodebug", "--nodebug"));
      System.out.println(getMessage("syntax.debug-mode", "--debug-mode"));
      System.out.println(getMessage("syntax.log", "--log"));
      System.out.println(getMessage("syntax.nolog", "--nolog"));
      System.out.println();
      System.out.println(getMessage("syntax.version", "--version", "-v"));
      System.out.println(getMessage("syntax.help", "--help", "-h"));
      System.out.println();
      System.out.println(getMessage("syntax.output.options"));
      System.out.println();
      System.out.println(getMessage("syntax.out", "--output", "-o"));
      System.out.println(getMessage("syntax.latex", "--latex"));
      System.out.println(getMessage("syntax.html", "--html"));
      System.out.println(getMessage("syntax.out.charset", "--out-charset"));
      System.out.println();
      System.out.println(getMessage("syntax.html.options"));
      System.out.println();
      System.out.println(getMessage("syntax.head", "--head"));
      System.out.println(getMessage("syntax.mathjax", "--[no]mathjax"));
      System.out.println(getMessage("syntax.entities", "--entities"));
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
         String.format("Copyright (C) 2013-%s Nicola L. C. Talbot (www.dickimaw-books.com)",
          APP_DATE.substring(0, 4)),
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

         if (debugMode > 0)
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
      if (debugMode > 0)
      {
         System.err.println(String.format("%s: %s", APP_NAME, message));
      }
   }

   public void debug(Exception e)
   {
      if (debugMode > 0)
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
         else if (args[i].equals("--log"))
         {
            if (logFile != null)
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

            logFile = new File(args[i]);
         }
         else if (args[i].equals("--nolog"))
         {
            logFile = null;
         }
         else if (args[i].equals("--latex"))
         {
            outputFormat = "latex";
         }
         else if (args[i].equals("--html"))
         {
            outputFormat = "html";
         }
         else if (args[i].equals("--nomathjax"))
         {
            mathJax = false;
         }
         else if (args[i].equals("--mathjax"))
         {
            mathJax = true;
         }
         else if (args[i].equals("--noentities"))
         {
            useHtmlEntities = false;
         }
         else if (args[i].equals("--entities"))
         {
            useHtmlEntities = true;
         }
         else if (args[i].equals("--head"))
         {
            i++;

            if (i == args.length)
            {
               throw new InvalidSyntaxException(
                 getMessage("error.syntax.missing_filename", args[i-1]));
            }

            extraHead = args[i];
         }
         else if (args[i].equals("--debug"))
         {
            debugMode = Integer.MAX_VALUE;

            if (i < args.length - 1)
            {
               try
               {
                  int val = Integer.parseInt(args[i+1]);

                  if (val >= 0)
                  {
                     debugMode = val;
                     i++;
                  }
               }
               catch (NumberFormatException e)
               {
               }
            }
         }
         else if (args[i].equals("--debug-mode"))
         {
            i++;

            if (i == args.length)
            {
               throw new InvalidSyntaxException(
                 getMessage("error.syntax.missing_mode", args[i-1]));
            }

            try
            {
               int val = Integer.parseInt(args[i]);

               if (val >= 0)
               {
                  debugMode = val;
               }
            }
            catch (NumberFormatException e)
            {
               debugMode = 0;

               String[] split = args[i].split(",");

               for (String mode : split)
               {
                  if (mode.equals("all"))
                  {
                     debugMode = Integer.MAX_VALUE;
                  }
                  else if (mode.equals("io"))
                  {
                     debugMode = debugMode | TeXParser.DEBUG_IO;
                  }
                  else if (mode.equals("popped"))
                  {
                     debugMode = debugMode | TeXParser.DEBUG_POPPED;
                  }
                  else if (mode.equals("decl"))
                  {
                     debugMode = debugMode | TeXParser.DEBUG_DECL;
                  }
                  else if (mode.equals("sty-data"))
                  {
                     debugMode = debugMode | TeXParser.DEBUG_STY_DATA;
                  }
                  else if (mode.equals("expansion"))
                  {
                     debugMode = debugMode | TeXParser.DEBUG_EXPANSION;
                  }
                  else if (mode.equals("expansion-list"))
                  {
                     debugMode = debugMode | TeXParser.DEBUG_EXPANSION_LIST;
                  }
                  else if (mode.equals("expansion-once"))
                  {
                     debugMode = debugMode | TeXParser.DEBUG_EXPANSION_ONCE;
                  }
                  else if (mode.equals("expansion-once-list"))
                  {
                     debugMode = debugMode | TeXParser.DEBUG_EXPANSION_ONCE_LIST;
                  }
                  else if (mode.equals("process"))
                  {
                     debugMode = debugMode | TeXParser.DEBUG_PROCESSING;
                  }
                  else if (mode.equals("process-stack"))
                  {
                     debugMode = debugMode | TeXParser.DEBUG_PROCESSING_STACK;
                  }
                  else if (mode.equals("process-stack-list"))
                  {
                     debugMode = debugMode | TeXParser.DEBUG_PROCESSING_STACK_LIST;
                  }
                  else if (mode.equals("cs"))
                  {
                     debugMode = debugMode | TeXParser.DEBUG_CS;
                  }
                  else if (mode.equals("process-generic-cs"))
                  {
                     debugMode = debugMode | TeXParser.DEBUG_PROCESSING_GENERIC_CS;
                  }
                  else if (mode.equals("catcode"))
                  {
                     debugMode = debugMode | TeXParser.DEBUG_CATCODE;
                  }
                  else
                  {
                     throw new InvalidSyntaxException(
                       getMessage("error.syntax.unknown_debug_mode", mode));
                  }
               }
            }
         }
         else if (args[i].equals("--nodebug"))
         {
            debugMode = 0;
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

            inFileName = new File(args[i]);
         }
         else if (args[i].equals("--out-charset"))
         {
            i++;

            if (i == args.length)
            {
               throw new InvalidSyntaxException(
                 getMessage("error.syntax.missing_input",
                   args[i-1]));
            }

            outCharset = Charset.forName(args[i]);
         }
         else if (args[i].equals("--no-rm-tmp-dir"))
         {
            deleteTempDirOnExit = false;
         }
         else if (args[i].equals("--rm-tmp-dir"))
         {
            deleteTempDirOnExit = true;
         }
         else if (args[i].equals("--no-convert-images"))
         {
            convertImages = false;
         }
         else if (args[i].equals("--convert-images"))
         {
            convertImages = true;
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

            inFileName = new File(args[i]);
         }
      }
   }

   private void runApplication()
   {
      try
      {
         doBatchProcess();
      }
      catch (InvalidSyntaxException e)
      {
         error(e.getMessage());

         System.exit(1);
      }
      catch (Exception e)
      {
         error(e);
      }
   }

   public static void main(String[] args)
   {
      final TeXParserApp app = new TeXParserApp();

      try
      {
         app.parseArgs(args);
      }
      catch (InvalidSyntaxException e)
      {
         app.error(e.getMessage());

         System.exit(1);
      }
      catch (Exception e)
      {
         app.error(e);

         System.exit(1);
      }

      app.runApplication();
   }

   // TeXParser class now has its own version and date.
   // As from 0.9.2.2b these now refer to the test application only.
   public static final String APP_VERSION = "0.9.2.7b";
   public static final String APP_NAME = "texparsertest";
   public static final String APP_DATE = "2023-08-22";

   public static long MAX_PROCESS_TIME=0L;

   private int debugMode = 0;

   private TeXParserAppSettings settings;

   private File inFileName;

   private File outDir;

   private Charset outCharset=null;

   private Vector<ProcessListener> currentProcessListeners;

   private ErrorListener errorListener;

   private TeXParserAppMessages messages;

   private File texmf;

   private String outputFormat = "latex";

   private int nameIdx=0;

   private String extraHead=null;

   private File tmpDir=null;

   private File logFile = null;

   private boolean deleteTempDirOnExit = true;
   private boolean convertImages = true;
   private boolean mathJax = true;
   private boolean useHtmlEntities = false;

   public static final Pattern PNG_INFO =
    Pattern.compile(".*: PNG image data, (\\d+) x (\\d+),.*");
   public static final Pattern JPEG_INFO =
    Pattern.compile(".*: JPEG image data, .*, (\\d+)x(\\d+),.*");
   public static final Pattern PDF_INFO =
    Pattern.compile(".*Page size:\\s+(\\d*\\.?\\d+) x (\\d*\\.?\\d+) pts.*", Pattern.DOTALL);
}
