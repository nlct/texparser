/*
    Copyright (C) 2013-2023 Nicola L.C. Talbot
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

import java.nio.charset.Charset;
import java.nio.file.Path;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

/**
 * Application interface for TeXParser.
 * An application that uses an instance of TeXParser to parse
 * files needs to supply a TeXParserListener (which determines how
 * to interpret the commands and active characters found in the
 * files) and that in turn requires a TeXApp, which deals with 
 * messages and I/O. It's the application's TeXApp that determines
 * what level of file access is permitted (on top of the JVM's
 * file access permissions) and whether or not external processes
 * can be spawned.
 */

public interface TeXApp
{
   /**
    * Gets the output of kpsewhich arg. This method may spawn a
    * sub-process to run kpsewhich with the required argument
    * (in which case it may throw an InterruptedException
    * if the process is interrupted)
    * or may provide its own implementation of kpsewhich
    * or may simply return null. This method is used for
    * finding a file on TeX's search (in which the case, the
    * argument is the filename) or for querying TeX variables,
    * such as <code>openin_any</code> (in which case,
    * the argument should be in the form
    * <code>-var-value=<em>var-name</em></code>).
    *
    * If you choose to run kpsewhich, you might want to consider
    * caching the result.
    *
    * @param arg the argument to pass to kpsewhich
    * @return the output of kpsewhich or null if the query failed or
    * if the action is not supported
    */
   public String kpsewhich(String arg)
     throws IOException,InterruptedException;

   /**
    * Converts the given EPS file to a PDF file.
    * For example, this method may create a process
    * that runs <code>epstopdf --output=pdfFile epsFile</code>
    * in which case it may throw an InterruptedException
    * if the process is interrupted.
    *
    * This method should check read and write permissions and may do
    * nothing if the file conversion request should be ignored.
    *
    * @param epsFile the source EPS file
    * @param pdfFile the destination PDF file
    */
   public void epstopdf(File epsFile, File pdfFile)
     throws IOException,InterruptedException;

   /**
    * Converts the given WMF file to a PDF file.
    * For example, this method may create a process
    * that runs <code>wmftoeps -o epsFile wmfFile</code>
    * in which case it may throw an InterruptedException
    * if the process is interrupted.
    *
    * This method should check read and write permissions and may do
    * nothing if the file conversion request should be ignored.
    */
   public void wmftoeps(File wmfFile, File epsFile)
     throws IOException,InterruptedException;

   /**
    * Converts the given image file.
    * For example, this method may create a process
    * that runs <code>magick [inOptions] inFile [outOptions] outFile</code>
    * in which case it may throw an InterruptedException
    * if the process is interrupted.
    *
    * This method should check read and write permissions and may do
    * nothing if the file conversion request should be ignored.
    *
    * @param inPage the required page number if the source file
    * contains multiple pages
    * @param inOptions a list of ImageMagick input options
    * @param inFile the source image file
    * @param outOptions a list of ImageMagick output options
    * @param outFile the destination file
    */
   public void convertimage(int inPage, String[] inOptions, File inFile,
     String[] outOptions, File outFile)
     throws IOException,InterruptedException;

   /**
    * Reports to the user that the listener
    * has substitute some source code. This is typically
    * when obsolete or deprecated LaTeX code is replaced by newer
    * commands with LaTeX2LaTeX.
    *
    * This method may do nothing if the information isn't deemed
    * significant.
    *
    * @param TeXParser the TeXParser object that's being used to
    * parse the source files
    * @param original the original code
    * @param replacement the replacement code
    */
   public void substituting(TeXParser parser, String original, String replacement);

   /**
    * Gets a message identified by a label that may require
    * parameters. This method should hook into the localisation
    * commands provided by your application.
    *
    * See the file lib/resources/dictionaries/texparserlib-en.xml 
    * for an example set of messages.
    */
   public String getMessage(String label, Object... params);

   /**
    * Requests input from the user. This method may ignore the
    * request and return null.
    *
    * @param message the prompt for the user
    * @return the response from the user or null
    */
   public String requestUserInput(String message)
     throws IOException;

   /**
    * Prints a message. The message may be written to STDOUT or
    * a GUI element or may be ignored.
    *
    * @param text the message
    */
   public void message(String text);

   /**
    * Prints a warning. The message may be written to STDERR or
    * a GUI element.
    *
    * @param parser the TeXParser being used to parse the files.
    * @param message the warning message that needs to be reported
    */
   public void warning(TeXParser parser, String message);

   /**
    * Reports that an exception has occurred.
    *
    * @param excpt the exception
    */
   public void error(Exception excpt);

   /**
    * Reports the progress for a potentially long-running process.
    * This method may do nothing.
    * @param percentage the percentage progress
    */
   public void progress(int percentage);

   /**
    * Copies a file.
    * This method should check read and write permissions or may do
    * nothing, issue a warning or throw an exception if copying isn't supported.
    *
    * @param orgFile the source file
    * @param newFile the destination file
    */
   public void copyFile(File orgFile, File newFile)
     throws IOException,InterruptedException;

   /**
    * Determines if read access is allowed for the given file.
    * @param path a TeXPath representation of the file
    * @return true if read access is allowed or false otherwise
    */
   public boolean isReadAccessAllowed(TeXPath path);

   /**
    * Determines if read access is allowed for the given file.
    * @param file the file
    * @return true if read access is allowed or false otherwise
    */
   public boolean isReadAccessAllowed(File file);

   /**
    * Determines if write access is allowed for the given file.
    * @param path a TeXPath representation of the file
    * @return true if write access is allowed or false otherwise
    */
   public boolean isWriteAccessAllowed(TeXPath path);

   /**
    * Determines if write access is allowed for the given file.
    * @param file the file
    * @return true if write access is allowed or false otherwise
    */
   public boolean isWriteAccessAllowed(File file);

   /**
    * Gets the default encoding. This may simply return
    * java.nio.charset.Charset.defaultCharset().
    *
    * @return the default encoding
    */
   public Charset getDefaultCharset();

   /**
    * Creates a buffered reader for the given file and encoding.
    * This method may simply use
    * java.nio.Files.newBufferedReader(Path,Charset)
    * or may impose extra restrictions.
    *
    * @param path the path to the file
    * @param charset the charset to use for decoding
    * @return a new buffered reader
    * @throws IOException if an I/O error occurs when opening the
    * file
    * @throws SecurityException if the request trips the
    * application's security protocol
    */
   public BufferedReader createBufferedReader(Path path,
     Charset charset) throws IOException, SecurityException;

   /**
    * Creates a buffered writer for the given file and encoding.
    * This method may simply use
    * java.nio.Files.newBufferedWriter(Path,Charset)
    * or may impose extra restrictions.
    *
    * @param path the path to the file
    * @param charset the charset to use for decoding
    * @return a new buffered writer
    * @throws IOException if an I/O error occurs when opening the
    * file
    * @throws SecurityException if the request trips the
    * application's security protocol
    */
   public BufferedWriter createBufferedWriter(Path path,
     Charset charset) throws IOException, SecurityException;

   /**
    * Gets the application name.
    */ 
   public String getApplicationName();

   /**
    * Gets the application version.
    */ 
   public String getApplicationVersion();

   /**
    * Localisation message label indicating that a file is about to
    * be read. Takes one parameter (the filename).
    */
   public static String MESSAGE_READING = "message.reading";

   /**
    * Localisation message label indicating that a file is about to
    * be written. Takes one parameter (the filename).
    */
   public static String MESSAGE_WRITING = "message.writing";

   /**
    * Localisation message label indicating that a file 
    * does not have read access. Takes one parameter (the filename).
    */
   public static String MESSAGE_NO_READ = "message.no.read";

   /**
    * Localisation message label indicating that a file 
    * does not have write access. Takes one parameter (the filename).
    */
   public static String MESSAGE_NO_WRITE = "message.no.write";

   /**
    * Localisation message label used to report the
    * selected encoding. Takes one parameter (the encoding name).
    */
   public static String MESSAGE_ENCODING = "message.charset";

   /**
    * Localisation message label indicating that a file 
    * is being overwritten. Takes one parameter (the filename).
    */
   public static String WARNING_OVERWRITING = "warning.overwriting";
}
