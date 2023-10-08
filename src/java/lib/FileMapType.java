/*
    Copyright (C) 2023 Nicola L.C. Talbot
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

/**
  The parse setting used by
  TeXParser.fileMap(TeXPath,FileMapType,FileMapHandler). 
  Any characters that have the EOL category code are considered a line break.
  Additionally, "\r\n" and "\n\r" are considered a single line break.

  The VERBATIM setting parses the file verbatim. Alphabetic characters 
  are assigned the "letter" category code and all other characters (including
  spaces) are assigned the "other" category code. No EOL characters will be
  included in the line passed to the FileMapHandler.

  For the remaining settings, the escape character is any character with the
  "escape" category code.

  The VERBATIM_EXCEPT_ESC_SEQ setting is like VERBATIM but any escape
  character will be treated as indicating a control sequence (either a
  control symbol or a control word). Note that any space following a
  control word will be treated literally and won't be ignored. So EOL
  following a control word will mark the end of the line. If an 
  escape character occurs at the end of the line, it will be considered
  a control sequence where the control name is the line break character
  (or characters in the event of "\r\n" or "\n\r").

  The VERBATIM_EXCEPT_ESC_SYM setting is like VERBATIM but any
  escape character will be treated as indicating a control symbol.
  Note that this only considers the first character that immediately
  follows the escape character. For example, "\no" will be parsed as
  the control sequence "\n" followed by the letter "o". If an 
  escape character occurs at the end of the line, it will be considered
  a control sequence where the control name is the line break character
  (or characters in the event of "\r\n" or "\n\r").

  The TEX setting parses the file using the current category codes.
  Any line break occurring with a group will be considered part of the 
  group and not a terminating EOL. Line breaks following the comment symbol
  or control words will be skipped as per usual.

 */
public enum FileMapType
{
   VERBATIM, VERBATIM_EXCEPT_ESC_SEQ, VERBATIM_EXCEPT_ESC_SYM, TEX;
}

