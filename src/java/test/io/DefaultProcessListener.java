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
package com.dickimawbooks.texparsertest.io;

import com.dickimawbooks.texparsertest.TeXParserApp;

public class DefaultProcessListener implements ProcessListener
{
   public DefaultProcessListener(TeXParserApp application)
   {
      this(application, -1, false);
   }

   public DefaultProcessListener(TeXParserApp application, boolean saveAllContent)
   {
      this(application, -1, saveAllContent);
   }

   public DefaultProcessListener(TeXParserApp application,
     int saveLineNum)
   {
      this(application, saveLineNum, false);
   }

   public DefaultProcessListener(TeXParserApp application,
     int saveLineNum, boolean saveAllContent)
   {
      app = application;
      this.saveLineNum = saveLineNum;

      if (saveAllContent)
      {
         allContent = new StringBuilder();
      }
   }

   public void setProcess(Process process)
   {
      this.process = process;
   }

   public void setThread(Thread thread)
   {
      this.thread = thread;
   }

   public void error(Exception e)
   {
      app.error(e);
   }

   public void terminateProcess()
   {
      if (thread != null)
      {
         app.debug("Interrupting "+thread);
         thread.interrupt();
      }

      if (process != null)
      {
         app.debug("Destroying "+process);
         process.destroy();
      }
   }

   public void processLine(int lineNum, String line)
   {
      if (lineNum == saveLineNum)
      {
         savedLine = line;
      }

      if (allContent != null)
      {
         allContent.append(String.format("%s%n", line));
      }
   }

   public String getSavedLine()
   {
      return savedLine;
   }

   public String getContent()
   {
      return allContent == null ? savedLine : allContent.toString();
   }

   public void processErrorLine(int lineNum, String line)
   {
      System.err.println(line);
   }

   public void setInterruptor(InterruptTimerTask interruptor)
   {
      this.interruptor = interruptor;
   }

   public InterruptTimerTask getInterruptor()
   {
      return interruptor;
   }

   private InterruptTimerTask interruptor;
   private TeXParserApp app;
   private Process process;
   private Thread thread;

   private StringBuilder allContent;
   private String savedLine=null;
   private int saveLineNum=-1;
}
