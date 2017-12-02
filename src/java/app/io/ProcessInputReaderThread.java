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

import java.io.*;

import com.dickimawbooks.texparserapp.TeXParserApp;

public class ProcessInputReaderThread extends Thread
{
   public ProcessInputReaderThread(TeXParserApp app, Process process, 
      ProcessListener listener)
   {
      super();
      this.app = app;
      this.process = process;
      this.listener = listener;
   }

   public void run()
   {
      BufferedReader in = null;
      BufferedReader err = null;

      try
      {
         in = new BufferedReader(new InputStreamReader(process.getInputStream()));

         String line;
         int lineNum = 0;

         app.checkForInterrupt();

         while ((line = in.readLine()) != null)
         {
            lineNum++;
            listener.processLine(lineNum, line);

            app.checkForInterrupt();
         }

         err = new BufferedReader(new InputStreamReader(process.getErrorStream()));

         lineNum = 0;

         app.checkForInterrupt();

         while ((line = err.readLine()) != null)
         {
            lineNum++;
            listener.processErrorLine(lineNum, line);
            app.checkForInterrupt();
         }
      }
      catch (Exception e)
      {
         InterruptTimerTask interruptor = listener.getInterruptor();

         if (interruptor != null)
         {
            switch (interruptor.getStatus())
            {
               case InterruptTimerTask.STATUS_ABORT:
                  listener.error(new CancelledException(app));
               return;
               case InterruptTimerTask.STATUS_TIMEDOUT:
                 listener.error(new CancelledException(
                    app.getMessage("error.timedout", 
                       TeXParserApp.MAX_PROCESS_TIME)));
               return;
            }
         }

         listener.error(e);
      }
      finally
      {
         if (in != null)
         {
            try
            {
               in.close();
            }
            catch (IOException e)
            {
               listener.error(e);
            }

            in = null;
         }

         if (err != null)
         {
            try
            {
               err.close();
            }
            catch (IOException e)
            {
               listener.error(e);
            }

            err = null;
         }
      }
   }

   private Process process;
   private ProcessListener listener;
   private TeXParserApp app;
}
