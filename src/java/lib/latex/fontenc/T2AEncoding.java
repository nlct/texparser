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
package com.dickimawbooks.texparserlib.latex.fontenc;

import com.dickimawbooks.texparserlib.TeXParserListener;
import com.dickimawbooks.texparserlib.FontEncoding;
import com.dickimawbooks.texparserlib.TeXSettings;

public class T2AEncoding extends FontEncoding
{
   public T2AEncoding()
   {
      super("T2A");
   }

   public void addDefinitions(TeXSettings settings)
   {
      TeXParserListener listener = settings.getParser().getListener();

      settings.putControlSequence(listener.createSymbol("CYRYO", 0x0401));
      settings.putControlSequence(listener.createSymbol("CYRDJE", 0x0402));
      settings.putControlSequence(listener.createSymbol("CYRIE", 0x0404));
      settings.putControlSequence(listener.createSymbol("CYRII", 0x0406));
      settings.putControlSequence(listener.createSymbol("CYRYI", 0x0407));
      settings.putControlSequence(listener.createSymbol("CYRJE", 0x0408));
      settings.putControlSequence(listener.createSymbol("CYRLJE", 0x0409));
      settings.putControlSequence(listener.createSymbol("CYRNJE", 0x040A));
      settings.putControlSequence(listener.createSymbol("CYRTSHE", 0x040B));
      settings.putControlSequence(listener.createSymbol("CYRUSHRT", 0x040E));
      settings.putControlSequence(listener.createSymbol("CYRDZHE", 0x040F));
      settings.putControlSequence(listener.createSymbol("CYRA", 0x0410));
      settings.putControlSequence(listener.createSymbol("CYRB", 0x0411));
      settings.putControlSequence(listener.createSymbol("CYRV", 0x0412));
      settings.putControlSequence(listener.createSymbol("CYRG", 0x0413));
      settings.putControlSequence(listener.createSymbol("CYRD", 0x0414));
      settings.putControlSequence(listener.createSymbol("CYRE", 0x0415));
      settings.putControlSequence(listener.createSymbol("CYRZH", 0x0416));
      settings.putControlSequence(listener.createSymbol("CYRZ", 0x0417));
      settings.putControlSequence(listener.createSymbol("CYRI", 0x0418));
      settings.putControlSequence(listener.createSymbol("CYRISHRT", 0x0419));
      settings.putControlSequence(listener.createSymbol("CYRK", 0x041A));
      settings.putControlSequence(listener.createSymbol("CYRL", 0x041B));
      settings.putControlSequence(listener.createSymbol("CYRM", 0x041C));
      settings.putControlSequence(listener.createSymbol("CYRN", 0x041D));
      settings.putControlSequence(listener.createSymbol("CYRO", 0x041E));
      settings.putControlSequence(listener.createSymbol("CYRP", 0x041F));
      settings.putControlSequence(listener.createSymbol("CYRR", 0x0420));
      settings.putControlSequence(listener.createSymbol("CYRS", 0x0421));
      settings.putControlSequence(listener.createSymbol("CYRT", 0x0422));
      settings.putControlSequence(listener.createSymbol("CYRU", 0x0423));
      settings.putControlSequence(listener.createSymbol("CYRF", 0x0424));
      settings.putControlSequence(listener.createSymbol("CYRH", 0x0425));
      settings.putControlSequence(listener.createSymbol("CYRC", 0x0426));
      settings.putControlSequence(listener.createSymbol("CYRCH", 0x0427));
      settings.putControlSequence(listener.createSymbol("CYRSH", 0x0428));
      settings.putControlSequence(listener.createSymbol("CYRSHCH", 0x0429));
      settings.putControlSequence(listener.createSymbol("CYRHRDSN", 0x042A));
      settings.putControlSequence(listener.createSymbol("CYRERY", 0x042B));
      settings.putControlSequence(listener.createSymbol("CYRSFTSN", 0x042C));
      settings.putControlSequence(listener.createSymbol("CYREREV", 0x042D));
      settings.putControlSequence(listener.createSymbol("CYRYU", 0x042E));
      settings.putControlSequence(listener.createSymbol("CYRYA", 0x042F));
      settings.putControlSequence(listener.createSymbol("cyra", 0x0430));
      settings.putControlSequence(listener.createSymbol("cyrb", 0x0431));
      settings.putControlSequence(listener.createSymbol("cyrv", 0x0432));
      settings.putControlSequence(listener.createSymbol("cyrg", 0x0433));
      settings.putControlSequence(listener.createSymbol("cyrd", 0x0434));
      settings.putControlSequence(listener.createSymbol("cyre", 0x0435));
      settings.putControlSequence(listener.createSymbol("cyrzh", 0x0436));
      settings.putControlSequence(listener.createSymbol("cyrz", 0x0437));
      settings.putControlSequence(listener.createSymbol("cyri", 0x0438));
      settings.putControlSequence(listener.createSymbol("cyrishrt", 0x0439));
      settings.putControlSequence(listener.createSymbol("cyrk", 0x043A));
      settings.putControlSequence(listener.createSymbol("cyrl", 0x043B));
      settings.putControlSequence(listener.createSymbol("cyrm", 0x043C));
      settings.putControlSequence(listener.createSymbol("cyrn", 0x043D));
      settings.putControlSequence(listener.createSymbol("cyro", 0x043E));
      settings.putControlSequence(listener.createSymbol("cyrp", 0x043F));
      settings.putControlSequence(listener.createSymbol("cyrr", 0x0440));
      settings.putControlSequence(listener.createSymbol("cyrs", 0x0441));
      settings.putControlSequence(listener.createSymbol("cyrt", 0x0442));
      settings.putControlSequence(listener.createSymbol("cyru", 0x0443));
      settings.putControlSequence(listener.createSymbol("cyrf", 0x0444));
      settings.putControlSequence(listener.createSymbol("cyrh", 0x0445));
      settings.putControlSequence(listener.createSymbol("cyrc", 0x0446));
      settings.putControlSequence(listener.createSymbol("cyrch", 0x0447));
      settings.putControlSequence(listener.createSymbol("cyrsh", 0x0448));
      settings.putControlSequence(listener.createSymbol("cyrshch", 0x0449));
      settings.putControlSequence(listener.createSymbol("cyrhrdsn", 0x044A));
      settings.putControlSequence(listener.createSymbol("cyrery", 0x044B));
      settings.putControlSequence(listener.createSymbol("cyrsftsn", 0x044C));
      settings.putControlSequence(listener.createSymbol("cyrerev", 0x044D));
      settings.putControlSequence(listener.createSymbol("cyryu", 0x044E));
      settings.putControlSequence(listener.createSymbol("cyrya", 0x044F));
      settings.putControlSequence(listener.createSymbol("cyryo", 0x0451));
      settings.putControlSequence(listener.createSymbol("cyrdje", 0x0452));
      settings.putControlSequence(listener.createSymbol("cyrie", 0x0454));
      settings.putControlSequence(listener.createSymbol("cyrdze", 0x0455));
      settings.putControlSequence(listener.createSymbol("cyrii", 0x0456));
      settings.putControlSequence(listener.createSymbol("cyryi", 0x0457));
      settings.putControlSequence(listener.createSymbol("cyrje", 0x0458));
      settings.putControlSequence(listener.createSymbol("cyrlje", 0x0459));
      settings.putControlSequence(listener.createSymbol("cyrnje", 0x045A));
      settings.putControlSequence(listener.createSymbol("cyrtshe", 0x045B));
      settings.putControlSequence(listener.createSymbol("cyrushrt", 0x045E));
      settings.putControlSequence(listener.createSymbol("cyrdzhe", 0x045F));
      settings.putControlSequence(listener.createSymbol("CYRGUP", 0x0490));
      settings.putControlSequence(listener.createSymbol("cyrgup", 0x0491));
      settings.putControlSequence(listener.createSymbol("CYRGHCRS", 0x0492));
      settings.putControlSequence(listener.createSymbol("cyrghcrs", 0x0493));
      settings.putControlSequence(listener.createSymbol("CYRZHDSC", 0x0496));
      settings.putControlSequence(listener.createSymbol("cyrzhdsc", 0x0497));
      settings.putControlSequence(listener.createSymbol("CYRZDSC", 0x0498));
      settings.putControlSequence(listener.createSymbol("cyrzdsc", 0x0499));
      settings.putControlSequence(listener.createSymbol("CYRKDSC", 0x049A));
      settings.putControlSequence(listener.createSymbol("cyrkdsc", 0x049B));
      settings.putControlSequence(listener.createSymbol("CYRKVCRS", 0x049C));
      settings.putControlSequence(listener.createSymbol("cyrkvcrs", 0x049D));
      settings.putControlSequence(listener.createSymbol("CYRKBEAK", 0x04A0));
      settings.putControlSequence(listener.createSymbol("cyrkbeak", 0x04A1));
      settings.putControlSequence(listener.createSymbol("CYRNDSC", 0x04A2));
      settings.putControlSequence(listener.createSymbol("cyrndsc", 0x04A3));
      settings.putControlSequence(listener.createSymbol("CYRNG", 0x04A4));
      settings.putControlSequence(listener.createSymbol("cyrng", 0x04A5));
      settings.putControlSequence(listener.createSymbol("CYRSDSC", 0x04AA));
      settings.putControlSequence(listener.createSymbol("cyrsdsc", 0x04AB));
      settings.putControlSequence(listener.createSymbol("CYRY", 0x04AE));
      settings.putControlSequence(listener.createSymbol("cyry", 0x04AF));
      settings.putControlSequence(listener.createSymbol("CYRYHCRS", 0x04B0));
      settings.putControlSequence(listener.createSymbol("cyryhcrs", 0x04B1));
      settings.putControlSequence(listener.createSymbol("CYRHDSC", 0x04B2));
      settings.putControlSequence(listener.createSymbol("cyrhdsc", 0x04B3));
      settings.putControlSequence(listener.createSymbol("CYRCHRDSC", 0x04B6));
      settings.putControlSequence(listener.createSymbol("cyrchrdsc", 0x04B7));
      settings.putControlSequence(listener.createSymbol("CYRCHVCRS", 0x04B8));
      settings.putControlSequence(listener.createSymbol("cyrchvcrs", 0x04B9));
      settings.putControlSequence(listener.createSymbol("CYRSHHA", 0x04BA));
      settings.putControlSequence(listener.createSymbol("cyrshha", 0x04BB));
      settings.putControlSequence(listener.createSymbol("CYRpalochka", 0x04C0));
      settings.putControlSequence(listener.createSymbol("CYRAE", 0x04D4));
      settings.putControlSequence(listener.createSymbol("cyrae", 0x04D5));
      settings.putControlSequence(listener.createSymbol("CYRSCHWA", 0x04D8));
      settings.putControlSequence(listener.createSymbol("cyrschwa", 0x04D9));
      settings.putControlSequence(listener.createSymbol("CYROTLD", 0x04E8));
      settings.putControlSequence(listener.createSymbol("cyrotld", 0x04E9));

   }
}
