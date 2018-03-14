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
package com.dickimawbooks.texparserapp.io;

import java.io.File;

import com.dickimawbooks.texparserapp.TeXParserApp;

public class KpsewhichListener implements ProcessListener
{
   public KpsewhichListener(TeXParserApp application)
   {
      app = application;
   }

   public void processLine(int lineNum, String line)
   {
      try
      {
         result = line;
      }
      catch (NumberFormatException e)
      {
      }
   }

   public void processErrorLine(int lineNum, String line)
   {
      System.err.println(line);
   }

   public void error(Exception e)
   {
      app.error(e);
   }

   public String getResult()
   {
      return result;
   }

   public void setProcess(Process process)
   {
      this.process = process;
   }

   public void setThread(Thread thread)
   {
      this.thread = thread;
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
   private String result = null;
   private Process process;
   private Thread thread;
}

