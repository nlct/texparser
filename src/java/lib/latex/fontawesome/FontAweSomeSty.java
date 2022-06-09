/*
    Copyright (C) 2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.fontawesome;

import java.io.IOException;
import java.util.HashMap;
import java.awt.Color;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class FontAweSomeSty extends LaTeXSty
{
   public FontAweSomeSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
    throws IOException
   {
      super(options, "fontawesome", listener, loadParentOptions);

      iconMap = new HashMap<String,TeXObject>();
   }

   @Override
   public void addDefinitions()
   {
      LaTeXParserListener listener = getListener();

      registerControlSequence(new FaIcon(this));

      for (int i = 0; i < SYMBOLS.length; i++)
      {
         String csname = "fa"+SYMBOLS[i][0];
         String iconname = SYMBOLS[i][1].toString();

         TeXObject sym;

         if (SYMBOLS[i][2] instanceof Integer)
         {
            int cp = ((Integer)SYMBOLS[i][2]).intValue();
            sym = listener.getOther(cp);
         }
         else
         {
            TeXObjectList list = listener.createStack();

            String str = SYMBOLS[i][2].toString();

            for (int j = 0; j < str.length(); )
            {
               int cp = str.codePointAt(j);
               j += Character.charCount(cp);

               list.add(listener.getOther(cp));
            }

            sym = list;
         }

         String encap = null;

         if (SYMBOLS[i].length > 3)
         {
            encap = SYMBOLS[i][3].toString();
         }

         TeXObject def;

         if (encap == null)
         {
            def = sym;
         }
         else
         {
            TeXObjectList list = listener.createStack();
            list.add(new TeXCsRef(encap));

            Group grp = listener.createGroup();
            list.add(grp);

            grp.add(sym);

            def = list;
         }

         registerControlSequence(new GenericCommand(true, csname, null, def));

         iconMap.put(iconname, def);
      }

      TeXObjectList list = listener.createStack();

      list.add(new BoxOverlap(0x2194));
      list.add(listener.getOther(0x2195));

      registerControlSequence(new GenericCommand(true, "faArrows", null, list));

      iconMap.put("arrows", list);

      list = listener.createStack();

      list.add(new BoxOverlap(0x2921));
      list.add(listener.getOther(0x2922));

      registerControlSequence(new GenericCommand(true, "faArrowsAlt", null, list));

      iconMap.put("arrows-alt", list);

      list = listener.createStack();

      list.add(new BoxOverlap(0x2197, "texparser@partial@overlapper"));
      list.add(listener.getOther(0x25A1));

      registerControlSequence(new GenericCommand(true, "faExternalLink", null, list));

      iconMap.put("external-link", list);

      FrameBox box = new FrameBox("fontawesome@disabledicon", 
        BorderStyle.NONE, AlignHStyle.DEFAULT, AlignVStyle.DEFAULT, true);

      box.setForegroundColor(Color.GRAY);
      box.setId("fwsdisabledicon");
      listener.declareFrameBox(box, false);

      box = new FrameBox("fontawesome@activeicon", 
        BorderStyle.NONE, AlignHStyle.DEFAULT, AlignVStyle.DEFAULT, true);

      box.setForegroundColor(Color.GREEN);
      box.setId("fwsactiveicon");
      listener.declareFrameBox(box, false);
   }

   public TeXObject getIconDefinition(String iconname)
   {
      TeXObject def = iconMap.get(iconname);

      if (def == null)
      {
         TeXApp texApp = getListener().getTeXApp();

         texApp.warning(getParser(), texApp.getMessage(UNKNOWN_ICON_NAME, iconname));
      }

      return def;
   }

   private HashMap<String,TeXObject> iconMap;

/* Closest Unicode matches (how similar they are to the 
 * fontawesome characters will depend on the font in use).
 * Appending 0xFE0E will force B&W characters rather then use emojis.
 */
   private static final Object[][] SYMBOLS = new Object[][]
   {
      new Object[]{"Adjust", "adjust", Integer.valueOf(0x25D1)},
      new Object[]{"Ambulance", "ambulance", Integer.valueOf(0x1F691)},
      new Object[]{"Anchor", "anchor", Integer.valueOf(0x2693)},

      new Object[]{"AngleDoubleDown", "angle-double-down", Integer.valueOf(0x300A),
       "texparser@quarterleft"},
      new Object[]{"AngleDoubleLeft", "angle-double-left", Integer.valueOf(0x300A)},
      new Object[]{"AngleDoubleUp", "angle-double-up", Integer.valueOf(0x300B),
       "texparser@quarterleft"},
      new Object[]{"AngleDoubleRight", "angle-double-right", Integer.valueOf(0x300B)},

      new Object[]{"AngleDown", "angle-down", Integer.valueOf(0x3008),
       "texparser@quarterleft"},
      new Object[]{"AngleLeft", "angle-left", Integer.valueOf(0x3008)},
      new Object[]{"AngleUp", "angle-up", Integer.valueOf(0x3009),
       "texparser@quarterleft"},
      new Object[]{"AngleRight", "angle-right", Integer.valueOf(0x3009)},

      new Object[]{"Archive", "archive", Integer.valueOf(0x1F5C3)},
      new Object[]{"ArrowsH", "arrows-h", Integer.valueOf(0x2194)},
      new Object[]{"ArrowsV", "arrows-v", Integer.valueOf(0x2195)},
      new Object[]{"Asterisk", "asterisk", Integer.valueOf(0x1F7B8)},
      new Object[]{"At", "at", Integer.valueOf(0xFF20), "textbf"},
      new Object[]{"AudioDescription", "audio-description",
        "AD\uD83D\uDDE7", "textsf"},
      new Object[]{"Automobile", "automobile", Integer.valueOf(0x1F698)},

      new Object[]{"Backward", "backward", Integer.valueOf(0x23EA)},
      new Object[]{"BalanceScale", "balance-scale", Integer.valueOf(0x2696)},
      new Object[]{"Ban", "ban", Integer.valueOf(0x1F6C7)},
      new Object[]{"Bank", "bank", Integer.valueOf(0x1F3E6)},
      new Object[]{"BarChart", "bar-chart", Integer.valueOf(0x1F4CA)},
      new Object[]{"BarChartO", "bar-chart-o", Integer.valueOf(0x1F4CA)},
      new Object[]{"Bars", "bars", Integer.valueOf(0x1D362)},
      new Object[]{"BatteryFull", "battery-full", Integer.valueOf(0x1F50B)},
      new Object[]{"BatteryQuarter", "battery-quarter", Integer.valueOf(0x1FAAB)},
      new Object[]{"Bed", "bed", Integer.valueOf(0x1F6CF)},
      new Object[]{"Beer", "beer", Integer.valueOf(0x1F37A)},
      new Object[]{"Behance", "behance", "B\u0113", "textsf"},
      new Object[]{"Bell", "bell", Integer.valueOf(0x1F514)},
      new Object[]{"BellO", "bell-o", Integer.valueOf(0x1F514)},
      new Object[]{"BellSlash", "bell-slash", Integer.valueOf(0x1F515)},
      new Object[]{"BellSlashO", "bell-slash-o", Integer.valueOf(0x1F515)},
      new Object[]{"BirthdayCake", "birthday-cake", Integer.valueOf(0x1F382)},
      new Object[]{"Bicycle", "bicycle", Integer.valueOf(0x1F6B2)},
      new Object[]{"Blind", "blind", Integer.valueOf(0x1F9AF)},
      new Object[]{"Bold", "bold", "B", "textbf"},
      new Object[]{"Bolt", "bolt", Integer.valueOf(0x26A1)},
      new Object[]{"Bomb", "bomb", Integer.valueOf(0x1F4A3)},
      new Object[]{"Book", "book", Integer.valueOf(0x1F4D5)},
      new Object[]{"Bookmark", "bookmark", Integer.valueOf(0x1F516)},
      new Object[]{"BookmarkO", "bookmark-o", Integer.valueOf(0x1F516)},
      new Object[]{"Briefcase", "briefcase", Integer.valueOf(0x1F4BC)},
      new Object[]{"Bug", "bug", Integer.valueOf(0x1F41B)},
      new Object[]{"Building", "building", Integer.valueOf(0x1F3E2)},
      new Object[]{"BuildingO", "building-o", Integer.valueOf(0x1F3E2)},
      new Object[]{"Bullhorn", "bullhorn", Integer.valueOf(0x1F56B)},
      new Object[]{"Bullseye", "bullseye", Integer.valueOf(0x1F78B)},
      new Object[]{"Bus", "bus", Integer.valueOf(0x1F68D)},

      new Object[]{"Cab", "cab", Integer.valueOf(0x1F696)},
      new Object[]{"Calculator", "calculator", Integer.valueOf(0x1F5A9)},
      new Object[]{"Calendar", "calendar", Integer.valueOf(0x1F4C5)},
      new Object[]{"CalendarO", "calendar-o", Integer.valueOf(0x1F4C5)},
      new Object[]{"Camera", "camera", Integer.valueOf(0x1F4F7)},
      new Object[]{"CameraRetro", "camera-retro", Integer.valueOf(0x1F4F7)},
      new Object[]{"Car", "car", Integer.valueOf(0x1F698)},

      // Medium black triangles
      new Object[]{"CaretDown", "caret-down", Integer.valueOf(0x1F783)},
      new Object[]{"CaretLeft", "caret-left", Integer.valueOf(0x1F780)},
      new Object[]{"CaretRight", "caret-right", Integer.valueOf(0x1F782)},
      new Object[]{"CaretUp", "caret-up", Integer.valueOf(0x1F781)},

      new Object[]{"CaretSquareODown", "caret-square-o-down", 
        Integer.valueOf(0x1F783), "texparser@boxed"},
      new Object[]{"CaretSquareOLeft", "caret-square-o-left", 
        Integer.valueOf(0x1F780), "texparser@boxed"},
      new Object[]{"CaretSquareORight", "caret-square-o-right", 
        Integer.valueOf(0x1F782), "texparser@boxed"},
      new Object[]{"CaretSquareOUp", "caret-square-o-up", 
        Integer.valueOf(0x1F781), "texparser@boxed"},

      new Object[]{"Cc", "cc", Integer.valueOf(0x1F4B3)},
      new Object[]{"Certificate", "certificate", Integer.valueOf(0x1F7D3)},
      new Object[]{"Chain", "chain", Integer.valueOf(0x1F517)},
      new Object[]{"ChainBroken", "chain-broken", Integer.valueOf(0x1F517),
        "texparser@overlap@strike"},

      new Object[]{"Check", "check", Integer.valueOf(0x1F5F8)},
      new Object[]{"CheckCircle", "check-circle", Integer.valueOf(0x1F5F8), 
        "texparser@circled"},
      new Object[]{"CheckSquare", "check-square", Integer.valueOf(0x2705)},
      new Object[]{"CheckSquareO", "check-square-o", Integer.valueOf(0x1F5F9)},

      new Object[]{"ChevronDown", "chevron-down", Integer.valueOf(0x2B9F)},
      new Object[]{"ChevronLeft", "chevron-left", Integer.valueOf(0x2B9C)},
      new Object[]{"ChevronRight", "chevron-right", Integer.valueOf(0x2B9E)},
      new Object[]{"ChevronUp", "chevron-up", Integer.valueOf(0x2B9D)},

      new Object[]{"ChevronCircleDown", "chevron-circle-down", 
         Integer.valueOf(0x2B9F), "texparser@circled"},
      new Object[]{"ChevronCircleLeft", "chevron-circle-left", 
         Integer.valueOf(0x2B9C), "texparser@circled"},
      new Object[]{"ChevronCircleRight", "chevron-circle-right", 
         Integer.valueOf(0x2B9E), "texparser@circled"},
      new Object[]{"ChevronCircleUp", "chevron-circle-up", 
         Integer.valueOf(0x2B9D), "texparser@circled"},


      new Object[]{"Child", "child", Integer.valueOf(0x1F9D2)},
      new Object[]{"Circle", "circle", Integer.valueOf(0x25CF)},
      new Object[]{"CircleO", "circle-o", Integer.valueOf(0x2B58)},
      new Object[]{"CircleThin", "circle-thin", Integer.valueOf(0x25CB)},

      new Object[]{"Clipboard", "clipboard", Integer.valueOf(0x1F4CB)},
      new Object[]{"ClockO", "clock-o", Integer.valueOf(0x1F558)},
      new Object[]{"Cloud", "cloud", Integer.valueOf(0x2601)},
      new Object[]{"Cny", "cny", Integer.valueOf(0x00A5)},
      new Object[]{"Code", "code", "</>", "texttt"},
      new Object[]{"Coffee", "coffee", Integer.valueOf(0x2615)},
      new Object[]{"Cog", "cog", Integer.valueOf(0x2699)},
      new Object[]{"Comment", "comment", Integer.valueOf(0x1F5E9)},
      new Object[]{"CommentO", "comment-o", Integer.valueOf(0x1F5E9)},
      new Object[]{"Commenting", "commenting", Integer.valueOf(0x1F4AC)},
      new Object[]{"CommentingO", "commenting-o", Integer.valueOf(0x1F4AC)},
      new Object[]{"Comments", "comments", Integer.valueOf(0x1F5EA)},
      new Object[]{"CommentsO", "comments-o", Integer.valueOf(0x1F5EA)},
      new Object[]{"Compass", "compass", Integer.valueOf(0x1F9ED)},
      new Object[]{"Compress", "compress", Integer.valueOf(0x1F5DC)},
      new Object[]{"Copyright", "copyright", Integer.valueOf(0x00A9)},
      new Object[]{"CreditCard", "credit-card", Integer.valueOf(0x1F4B3)},
      new Object[]{"CreditCardAlt", "credit-card-alt", Integer.valueOf(0x1F4B3)},
      new Object[]{"Crosshairs", "crosshairs", Integer.valueOf(0x2316)},
      new Object[]{"Cut", "cut", Integer.valueOf(0x2702)},
      new Object[]{"Cutlery", "cutlery", Integer.valueOf(0x1F374)},

      new Object[]{"Deaf", "deaf", Integer.valueOf(0x1F9BB)},
      new Object[]{"Deafness", "deafness", Integer.valueOf(0x1F9BB)},
      new Object[]{"Desktop", "desktop", Integer.valueOf(0x1F5B5)},
      new Object[]{"Diamond", "diamond", Integer.valueOf(0x1F48E)},
      new Object[]{"Digg", "digg", "digg", "textsf"},
      new Object[]{"Dollar", "dollar", Integer.valueOf(0x1F4B2)},
      new Object[]{"DotCircleO", "dot-circle-o", Integer.valueOf(0x1F78A)},
      new Object[]{"Download", "download", Integer.valueOf(0x1F4E5)},
      new Object[]{"Dribbble", "dribbble", Integer.valueOf(0x1F3C0)},

      new Object[]{"Edit", "edit", Integer.valueOf(0x1F4DD)},
      new Object[]{"Eject", "eject", Integer.valueOf(0x23CF)},
      new Object[]{"EllipsisH", "ellipsis-h", Integer.valueOf(0x2026)},
      new Object[]{"EllipsisV", "ellipsis-v", Integer.valueOf(0xFE19)},
      new Object[]{"Envelope", "envelope", Integer.valueOf(0x1F582)},
      new Object[]{"EnvelopeO", "envelope-o", Integer.valueOf(0x1F582)},
      new Object[]{"Envira", "envira", Integer.valueOf(0x1F342)},
      new Object[]{"Eur", "eur", Integer.valueOf(0x20AC)},
      new Object[]{"Euro", "euro", Integer.valueOf(0x20AC)},
      new Object[]{"Exchange", "exchange", Integer.valueOf(0x21C4)},
      new Object[]{"Exclamation", "exclamation", Integer.valueOf(0x2757)},
      new Object[]{"ExclamationCircle", "exclamation-circle", Integer.valueOf('!'),
       "texparser@circled"},
      new Object[]{"ExclamationTriangle", "exclamation-triangle", Integer.valueOf(0x26A0)},
      new Object[]{"Expeditedssl", "expeditedssl", "\uD83D\uDD12\uFE0E",
        "texparser@circled"},
      new Object[]{"ExternalLinkSquare", "external-link-square", "\u2197",
         "texparser@boxed"},
      new Object[]{"Eye", "eye", Integer.valueOf(0x1F441)},
      new Object[]{"EyeSlash", "eye-slash", Integer.valueOf(0x1F441), 
        "texparser@overlap@strike"},

      new Object[]{"Fa", "fa", Integer.valueOf(0x2691)},
      new Object[]{"Facebook", "facebook", "f", "textsf"},
      new Object[]{"FacebookF", "facebook-f", "f", "textsf"},
      new Object[]{"FastBackward", "fast-backward", Integer.valueOf(0x23EE)},
      new Object[]{"FastForward", "fast-forward", Integer.valueOf(0x23ED)},
      new Object[]{"Fax", "fax", Integer.valueOf(0x1F4E0)},
      new Object[]{"Female", "female", Integer.valueOf(0x1F6BA)},
      new Object[]{"FighterJet", "fighter-jet", Integer.valueOf(0x1F6E6),
       "texparser@quarterright"},
      new Object[]{"File", "file", Integer.valueOf(0x1F5CB)},
      new Object[]{"FileImageO", "file-image-o", Integer.valueOf(0x1F5BB)},
      new Object[]{"FileO", "file-o", Integer.valueOf(0x1F5CB)},
      new Object[]{"FilePdfO", "file-pdf-o", Integer.valueOf(0x1F5BA)},
      new Object[]{"FilePhotoO", "file-photo-o", Integer.valueOf(0x1F5BB)},
      new Object[]{"FilePictureO", "file-picture-o", Integer.valueOf(0x1F5BB)},
      new Object[]{"FileTextO", "file-text-o", Integer.valueOf(0x1F5B9)},
      new Object[]{"FileText", "file-text", Integer.valueOf(0x1F5B9)},
      new Object[]{"FileWordO", "file-word-o", Integer.valueOf(0x1F5BA)},
      new Object[]{"FilesO", "files-o", Integer.valueOf(0x1F5CD)},
      new Object[]{"Film", "film", Integer.valueOf(0x1F39E)},
      new Object[]{"Fire", "fire", Integer.valueOf(0x1F525)},
      new Object[]{"FireExtinguisher", "fire-extinguisher", Integer.valueOf(0x1F9EF)},
      new Object[]{"Flag", "flag", Integer.valueOf(0x2691)},
      new Object[]{"FlagO", "flag-o", Integer.valueOf(0x2690)},
      new Object[]{"FlagCheckered", "flag-checkered", Integer.valueOf(0x1F3C1)},
      new Object[]{"Flash", "flash", Integer.valueOf(0x26A1)},
      new Object[]{"FloppyO", "floppy-o", Integer.valueOf(0x1F4BE)},
      new Object[]{"Folder", "folder", Integer.valueOf(0x1F4C1)},
      new Object[]{"FolderO", "folder-o", Integer.valueOf(0x1F4C1)},
      new Object[]{"FolderOpen", "folder-open", Integer.valueOf(0x1F4C2)},
      new Object[]{"FolderOpenO", "folder-open-o", Integer.valueOf(0x1F4C2)},
      new Object[]{"Font", "font", Integer.valueOf('A')},
      new Object[]{"Forward", "forward", Integer.valueOf(0x23E9)},
      new Object[]{"FrownO", "frown-o", Integer.valueOf(0x1F641)},
      new Object[]{"FutbolO", "futbol-o", Integer.valueOf(0x26BD)},

      new Object[]{"Gamepad", "gamepad", Integer.valueOf(0x1F3AE)},
      new Object[]{"Gbp", "gbp", Integer.valueOf(0x00A3)},
      new Object[]{"Gear", "gear", Integer.valueOf(0x2699)},
      new Object[]{"Genderless", "genderless", Integer.valueOf(0x26AA)},
      new Object[]{"Gift", "gift", Integer.valueOf(0x1F381)},
      new Object[]{"Git", "git", "git"},
      new Object[]{"Glass", "glass", Integer.valueOf(0x1F378)},
      new Object[]{"GlideG", "glide-g", "g"},
      new Object[]{"Globe", "globe", Integer.valueOf(0x1F30E)},
      new Object[]{"Google", "google", "G", "textsf"},
      new Object[]{"GooglePlus", "google-plus", "G+", "textsf"},
      new Object[]{"GraduationCap", "graduation-cap", Integer.valueOf(0x1F393)},

      new Object[]{"HandGrabO", "hand-grab-o", Integer.valueOf(0x270A)},
      new Object[]{"HandODown", "hand-o-down", Integer.valueOf(0x261F)},
      new Object[]{"HandOLeft", "hand-o-left", Integer.valueOf(0x261C)},
      new Object[]{"HandORight", "hand-o-right", Integer.valueOf(0x261E)},
      new Object[]{"HandOUp", "hand-o-up", Integer.valueOf(0x261D)},
      new Object[]{"HandPaperO", "hand-paper-o", Integer.valueOf(0x270B)},
      new Object[]{"HandPeaceO", "hand-peace-o", Integer.valueOf(0x270C)},
      new Object[]{"HandPointerO", "hand-pointer-o", Integer.valueOf(0x1F446)},
      new Object[]{"HandRockO", "hand-rock-o", Integer.valueOf(0x270A)},
      new Object[]{"HandScissorsO", "hand-scissors-o", Integer.valueOf(0x270C)},
      new Object[]{"HandSpockO", "hand-spock-o", Integer.valueOf(0x1F596)},
      new Object[]{"HandStopO", "hand-stop-o", Integer.valueOf(0x1F590)},
      new Object[]{"HardOfHearing", "hard-of-hearing", Integer.valueOf(0x1F9BB)},
      new Object[]{"Hashtag", "hashtag", Integer.valueOf('#')},
      new Object[]{"HddO", "hdd-o", Integer.valueOf(0x1F5B4)},
      new Object[]{"Header", "header", "H"},
      new Object[]{"Headphones", "headphones", Integer.valueOf(0x1F3A7)},
      new Object[]{"Heart", "heart", Integer.valueOf(0x2665)},
      new Object[]{"HeartO", "heart-o", Integer.valueOf(0x2661)},
      new Object[]{"History", "history", "\uD83D\uDD58\uFE0E\u20D4"},
      new Object[]{"Home", "home", Integer.valueOf(0x1F3E0)},
      new Object[]{"HospitalO", "hospital-o", Integer.valueOf(0x1F3E5)},
      new Object[]{"Hotel", "hotel", Integer.valueOf(0x1F3E8)},
      new Object[]{"Hourglass", "hourglass", Integer.valueOf(0x29D7)},
      new Object[]{"HourglassEnd", "hourglass-end", Integer.valueOf(0x231B)},
      new Object[]{"HourglassHalf", "hourglass-half", Integer.valueOf(0x23F3)},
      new Object[]{"HourglassO", "hourglass-o", Integer.valueOf(0x29D6)},

      new Object[]{"Image", "image", Integer.valueOf(0x1F5BB)},
      new Object[]{"Inbox", "inbox", Integer.valueOf(0x1F4E5)},
      new Object[]{"Industry", "industry", Integer.valueOf(0x1F3ED)},
      new Object[]{"Info", "info", Integer.valueOf(0x2139)},
      new Object[]{"InfoCircle", "info-circle", Integer.valueOf(0x1F6C8)},
      new Object[]{"Institution", "institution", Integer.valueOf(0x1F3DB)},
      new Object[]{"Intersex", "intersex", Integer.valueOf(0x26A5)},
      new Object[]{"Italic", "italic", "I", "textit"},

      new Object[]{"Jpy", "jpy", Integer.valueOf(0x00A5)},

      new Object[]{"Key", "key", Integer.valueOf(0x1F5DD)},
      new Object[]{"KeyboardO", "keyboard-o", Integer.valueOf(0x2328)},
      new Object[]{"Krw", "krw", Integer.valueOf(0x20A9)},

      new Object[]{"Laptop", "laptop", Integer.valueOf(0x1F4BB)},
      new Object[]{"Leaf", "leaf", Integer.valueOf(0x1F342)},
      new Object[]{"LemonO", "lemon-o", Integer.valueOf(0x1F34B)},
      new Object[]{"LevelDown", "level-down", Integer.valueOf(0x2BA7)},
      new Object[]{"LevelUp", "level-up", Integer.valueOf(0x2BA5)},
      new Object[]{"LifeBouy", "life-bouy", Integer.valueOf(0x1F6DF)},
      new Object[]{"LifeBuoy", "life-buoy", Integer.valueOf(0x1F6DF)},
      new Object[]{"LifeRing", "life-ring", Integer.valueOf(0x1F6DF)},
      new Object[]{"LifeSaver", "life-saver", Integer.valueOf(0x1F6DF)},
      new Object[]{"LightbulbO", "lightbulb-o", Integer.valueOf(0x1F4A1)},
      new Object[]{"LineChart", "line-chart", Integer.valueOf(0x1F4C8)},
      new Object[]{"Link", "link", Integer.valueOf(0x1F517)},
      new Object[]{"Linkedin", "linkedin", "in", "textsf"},
      new Object[]{"LocationArrow", "location-arrow", Integer.valueOf(0x2B9E), 
        "texparser@eighthleft"},
      new Object[]{"Lock", "lock", Integer.valueOf(0x1F512)},
      new Object[]{"LongArrowDown", "long-arrow-down", Integer.valueOf(0x2193)},
      new Object[]{"LongArrowLeft", "long-arrow-left", Integer.valueOf(0x2190)},
      new Object[]{"LongArrowRight", "long-arrow-right", Integer.valueOf(0x2192)},
      new Object[]{"LongArrowUp", "long-arrow-up", Integer.valueOf(0x2191)},

      new Object[]{"Magic", "magic", Integer.valueOf(0x1FA84)},
      new Object[]{"Magnet", "magnet", Integer.valueOf(0x1F9F2)},
      new Object[]{"MailForward", "mail-forward", Integer.valueOf(0x2BAB)},
      new Object[]{"MailReply", "mail-reply", Integer.valueOf(0x2BAA)},
      new Object[]{"Male", "male", Integer.valueOf(0x1F6B9)},
      new Object[]{"MapPin", "map-pin", Integer.valueOf(0x1F4CD)},
      new Object[]{"Mars", "mars", Integer.valueOf(0x2642)},
      new Object[]{"MarsDouble", "mars-double", Integer.valueOf(0x26A3)},
      new Object[]{"MarsStroke", "mars-stroke", Integer.valueOf(0x26A6)},
      new Object[]{"MarsStrokeH", "mars-stroke-h", Integer.valueOf(0x26A9)},
      new Object[]{"MarsStrokeV", "mars-stroke-v", Integer.valueOf(0x26A8)},
      new Object[]{"Maxcdn", "maxcdn", "m", "textsl"},
      new Object[]{"MehO", "meh-o", Integer.valueOf(0x1F610)},
      new Object[]{"Mercury", "mercury", Integer.valueOf(0x263F)},
      new Object[]{"Microphone", "microphone", Integer.valueOf(0x1F399)},
      new Object[]{"MicrophoneSlash", "microphone-slash", Integer.valueOf(0x1F399),
        "texparser@overlap@strike"},
      new Object[]{"Minus", "minus", Integer.valueOf(0x2796)},
      new Object[]{"MinusCircle", "minus-circle", Integer.valueOf(0x26DA)},
      new Object[]{"MinusSquareO", "minus-square-o", Integer.valueOf(0x229F)},
      new Object[]{"Mobile", "mobile", Integer.valueOf(0x1F4F1)},
      new Object[]{"MobilePhone", "mobile-phone", Integer.valueOf(0x1F4F1)},
      new Object[]{"Money", "money", Integer.valueOf(0x1F4B5)},
      new Object[]{"MoonO", "moon-o", Integer.valueOf(0x263E)},
      new Object[]{"MortarBoard", "mortar-board", Integer.valueOf(0x1F393)},
      new Object[]{"MousePointer", "mouse-pointer", Integer.valueOf(0x1F81D),
          "texparser@eighthright"},
      new Object[]{"Music", "music", Integer.valueOf(0x1F3B5)},

      new Object[]{"Navicon", "navicon", Integer.valueOf(0x1D362)},
      new Object[]{"Neuter", "neuter", Integer.valueOf(0x26B2)},
      new Object[]{"NewspaperO", "newspaper-o", Integer.valueOf(0x1F4F0)},

      new Object[]{"PaintBrush", "paint-brush", Integer.valueOf(0x1F58C)},
      new Object[]{"Paperclip", "paperclip", Integer.valueOf(0x1F4CE)},
      new Object[]{"Paragraph", "paragraph", Integer.valueOf(0x00B6)},
      new Object[]{"Pause", "pause", Integer.valueOf(0x23F8)},
      new Object[]{"PauseCircleO", "pause-circle-o", Integer.valueOf(0x23F8),
         "texparser@circled"},
      new Object[]{"Paw", "paw", Integer.valueOf(0x1F43E)},
      new Object[]{"Pencil", "pencil", Integer.valueOf(0x1F589)},
      new Object[]{"PencilSquare", "pencil-square", Integer.valueOf(0x1F4DD)},
      new Object[]{"Percent", "percent", Integer.valueOf(0x0025)},
      new Object[]{"Phone", "phone", Integer.valueOf(0x1F4DE)},
      new Object[]{"Photo", "photo", Integer.valueOf(0x1F5BB)},
      new Object[]{"PictureO", "picture-o", Integer.valueOf(0x1F5BB)},
      new Object[]{"Plane", "plane", Integer.valueOf(0x1F6EA)},
      new Object[]{"Play", "play", Integer.valueOf(0x23F5)},
      new Object[]{"Plug", "plug", Integer.valueOf(0x1F50C)},
      new Object[]{"Plus", "plus", Integer.valueOf(0x2795)},
      new Object[]{"PlusSquareO", "plus-square-o", Integer.valueOf(0x229E)},
      new Object[]{"PowerOff", "power-off", Integer.valueOf(0x23FB)},
      new Object[]{"Print", "print", Integer.valueOf(0x1F5A8)},
      new Object[]{"PuzzlePiece", "puzzle-piece", Integer.valueOf(0x1F9E9)},

      new Object[]{"Question", "question", Integer.valueOf(0x2753)},
      new Object[]{"QuoteLeft", "quote-left", Integer.valueOf(0x275D)},
      new Object[]{"QuoteRight", "quote-right", Integer.valueOf(0x275E)},

      new Object[]{"Random", "random", Integer.valueOf(0x1F500)},
      new Object[]{"Recycle", "recycle", Integer.valueOf(0x267B)},
      new Object[]{"Refresh", "refresh", Integer.valueOf(0x1F5D8)},
      new Object[]{"Registered", "registered", Integer.valueOf(0x00AE)},
      new Object[]{"Remove", "remove", Integer.valueOf(0x2716)},
      new Object[]{"Reorder", "reorder", Integer.valueOf(0x1D362)},
      new Object[]{"Repeat", "repeat", Integer.valueOf(0x27F3)},
      new Object[]{"Reply", "reply", Integer.valueOf(0x2BAA)},
      new Object[]{"Rmb", "rmb", Integer.valueOf(0x00A5)},
      new Object[]{"Road", "road", Integer.valueOf(0x1F6E3)},
      new Object[]{"Rocket", "rocket", Integer.valueOf(0x1F680)},
      new Object[]{"RotateLeft", "rotate-left", Integer.valueOf(0x27F2)},
      new Object[]{"RotateRight", "rotate-right", Integer.valueOf(0x27F3)},
      new Object[]{"Rouble", "rouble", Integer.valueOf(0x20BD)},
      new Object[]{"Rub", "rub", Integer.valueOf(0x20BD)},
      new Object[]{"Ruble", "ruble", Integer.valueOf(0x20BD)},

      new Object[]{"Save", "save", Integer.valueOf(0x1F5AB)},
      new Object[]{"Scissors", "scissors", Integer.valueOf(0x2702)},
      new Object[]{"Search", "search", Integer.valueOf(0x1F50D)},
      new Object[]{"Server", "server", Integer.valueOf(0x1F5A7)},
      new Object[]{"Share", "share", Integer.valueOf(0x2BAA)},
      new Object[]{"Shekel", "shekel", Integer.valueOf(0x20AA)},
      new Object[]{"Sheqel", "sheqel", Integer.valueOf(0x20AA)},
      new Object[]{"Shield", "shield", Integer.valueOf(0x1F6E1)},
      new Object[]{"Ship", "ship", Integer.valueOf(0x1F6F3)},
      new Object[]{"ShoppingBag", "shopping-bag", Integer.valueOf(0x1F6CD)},
      new Object[]{"ShoppingCart", "shopping-cart", Integer.valueOf(0x1F6D2)},
      new Object[]{"Signal", "signal", Integer.valueOf(0x1F4F6)},
      new Object[]{"Sliders", "sliders", Integer.valueOf(0x1F39A)},
      new Object[]{"SmileO", "smile-o", Integer.valueOf(0x1F642)},
      new Object[]{"SoccerBallO", "soccer-ball-o", Integer.valueOf(0x26BD)},
      new Object[]{"SortAsc", "sort-asc", Integer.valueOf(0x23F6)},
      new Object[]{"SortDesc", "sort-desc", Integer.valueOf(0x23F7)},
      new Object[]{"SortDown", "sort-down", Integer.valueOf(0x23F7)},
      new Object[]{"SortUp", "sort-up", Integer.valueOf(0x23F6)},
      new Object[]{"SpaceShuttle", "space-shuttle", Integer.valueOf(0x1F680)},
      new Object[]{"Spoon", "spoon", Integer.valueOf(0x1F944)},
      new Object[]{"Square", "square", Integer.valueOf(0x2B1B)},
      new Object[]{"SquareO", "square-o", Integer.valueOf(0x2B1C)},
      new Object[]{"Star", "star", Integer.valueOf(0x2B51)},
      new Object[]{"StarHalf", "star-half", Integer.valueOf(0x2BE8)},
      new Object[]{"StarHalfEmpty", "star-half-empty", Integer.valueOf(0x2BEA)},
      new Object[]{"StarHalfFull", "star-half-full", Integer.valueOf(0x2BEA)},
      new Object[]{"StarHalfO", "star-half-o", Integer.valueOf(0x2BEA)},
      new Object[]{"StarO", "star-o", Integer.valueOf(0x2B52)},
      new Object[]{"Stethoscope", "stethoscope", Integer.valueOf(0x1FA7A)},
      new Object[]{"StickyNote", "sticky-note", Integer.valueOf(0x1F5C8)},
      new Object[]{"StickyNoteO", "sticky-note-o", Integer.valueOf(0x1F5C5)},
      new Object[]{"Stop", "stop", Integer.valueOf(0x23F9)},
      new Object[]{"Subway", "subway", Integer.valueOf(0x1F687)},
      new Object[]{"Suitcase", "suitcase", Integer.valueOf(0x1F4BC)},
      new Object[]{"SunO", "sun-o", Integer.valueOf(0x1F323)},

      new Object[]{"Tag", "tag", Integer.valueOf(0x1F516)},
      new Object[]{"Taxi", "taxi", Integer.valueOf(0x1F696)},
      new Object[]{"Television", "television", Integer.valueOf(0x1F4FA)},
      new Object[]{"ThumbTack", "thumb-tack", Integer.valueOf(0x1F4CC)},
      new Object[]{"ThumbsODown", "thumbs-o-down", Integer.valueOf(0x1F44E)},
      new Object[]{"ThumbsOUp", "thumbs-o-up", Integer.valueOf(0x1F44D)},
      new Object[]{"Ticket", "ticket", Integer.valueOf(0x1F3AB)},
      new Object[]{"Times", "times", Integer.valueOf(0x274C)},
      new Object[]{"Tint", "tint", Integer.valueOf(0x1F322)},
      new Object[]{"ToggleOff", "toggle-off", "\uD83D\uDD18\uFE0E",
       "fontawesome@disabledicon"},
      new Object[]{"ToggleOn", "toggle-on", Integer.valueOf(0x1F518),
       "fontawesome@activeicon"},
      new Object[]{"Train", "train", Integer.valueOf(0x1F689)},
      new Object[]{"Transgender", "transgender", Integer.valueOf(0x26A5)},
      new Object[]{"TransgenderAlt", "transgender-alt", Integer.valueOf(0x26A7)},
      new Object[]{"Trash", "trash", Integer.valueOf(0x1F5D1)},
      new Object[]{"TrashO", "trash-o", Integer.valueOf(0x1F5D1)},
      new Object[]{"Tree", "tree", Integer.valueOf(0x1F332)},
      new Object[]{"Trophy", "trophy", Integer.valueOf(0x1F3C6)},
      new Object[]{"Truck", "truck", Integer.valueOf(0x1F69A)},
      new Object[]{"Try", "try", Integer.valueOf(0x20BA)},
      new Object[]{"TurkishLira", "turkish-lira", Integer.valueOf(0x20BA)},
      new Object[]{"Tv", "tv", Integer.valueOf(0x1F4FA)},

      new Object[]{"Umbrella", "umbrella", Integer.valueOf(0x2602)},
      new Object[]{"Undo", "undo", Integer.valueOf(0x27F2)},
      new Object[]{"University", "university", Integer.valueOf(0x1F3DB)},
      new Object[]{"Unlock", "unlock", Integer.valueOf(0x1F513)},
      new Object[]{"UnlockAlt", "unlock-alt", Integer.valueOf(0x1F513)},
      new Object[]{"Upload", "upload", Integer.valueOf(0x1F4E4)},
      new Object[]{"Usd", "usd", Integer.valueOf(0x1F4B2)},
      new Object[]{"User", "user", Integer.valueOf(0x1F464)},
      new Object[]{"Users", "users", Integer.valueOf(0x1F465)},

      new Object[]{"Venus", "venus", Integer.valueOf(0x2640)},
      new Object[]{"VenusDouble", "venus-double", Integer.valueOf(0x26A2)},
      new Object[]{"VenusMars", "venus-mars", Integer.valueOf(0x26A4)},
      new Object[]{"VideoCamera", "video-camera", Integer.valueOf(0x1F4F9)},
      new Object[]{"VolumeDown", "volume-down", Integer.valueOf(0x1F509)},
      new Object[]{"VolumeOff", "volume-off", Integer.valueOf(0x1F508)},
      new Object[]{"VolumeUp", "volume-up", Integer.valueOf(0x1F50A)},

      new Object[]{"Warning", "warning", Integer.valueOf(0x26A0)},
      new Object[]{"Wheelchair", "wheelchair", Integer.valueOf(0x1F9BD)},
      new Object[]{"WheelchairAlt", "wheelchair-alt", Integer.valueOf(0x267F)},
      new Object[]{"Won", "won", Integer.valueOf(0x20A9)},
      new Object[]{"Wrench", "wrench", Integer.valueOf(0x1F527)},
      new Object[]{"Yen", "yen", Integer.valueOf(0x00A5)}
   };

   public static final String UNKNOWN_ICON_NAME="fontawesome.unknown.icon_name";
}
