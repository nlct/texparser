/*
    Copyright (C) 2013-20 Nicola L.C. Talbot
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

import java.io.IOException;

/*
   TeXObjectList represents a stack of pending items that need to be
   processed. TeXParser is a sub-class of TeXObjectList so it's also
   a stack. Objects can be pushed to the current stack with
   TeXObjectList.push(TeXObject). Pending objects can also be pushed
   to a local stack (TeXObjectList) rather than the parser
   (TeXParser). If there's no local stack, the object is processed
   using TeXObject.process(TeXParser) otherwise the object is
   processed using TeXObject.process(TeXParser,TeXObjectList).
  
   Similarly, if an object is an instance of Expandable, it can be
   expanded with or without a local stack, and can either be
   expanded once or fully. Note that this library has a different
   way of expanding and processes objects to the way TeX works. The
   aim of this parser library is to obtain information from TeX
   documents not to typeset.
  
   Items can be popped using various methods. The basic method is
   pop() but this will also return Ignoreable objects such as comments or
   ignored spaces (spaces that follow control words or at the 
   start of a line). This is only useful if you actually need to keep
   these objects (e.g. with the LaTeX2LaTeX parser). Most of the
   other popping methods have a PopStyle argument that indicates the pop
   style. The constructor is new PopStyle(byte) or new PopStyle(int) where
   the value may be a bitwise OR combination of the following:
  
     POP_DEFAULT
     This is just 0 which allows paragraph breaks and leading
     non-ignoreable whitespace. Ignoreables are skipped.
  
     POP_SHORT
       Don't allow paragraph breaks
   
     POP_RETAIN_IGNOREABLES
       Allow ignoreables
   
     POP_IGNORE_LEADING_SPACE
       Skip any leading space.
  
   For example POP_SHORT | POP_IGNORE_LEADING_SPACE forbids \par but
   skips any leading space. For convenience there are static constants for
   the most common styles:

     PopStyle.DEFAULT
     PopStyle.SHORT
     PopStyle.RETAIN_IGNOREABLES
     PopStyle.IGNORE_LEADING_SPACE
     PopStyle.IGNORE_LEADING_PAR
     PopStyle.SHORT_RETAIN_IGNOREABLES
     PopStyle.SHORT_IGNORE_LEADING_SPACE
     PopStyle.IGNORE_LEADING_SPACE_RETAIN_IGNOREABLES
     PopStyle.IGNORE_LEADING_SPACE_AND_PAR

   A new style can be derived from an existing style with a particular setting
   excluded using PopStyle.excludeStyle(PopStyle). For example, 
   if popStyle = PopStyle.SHORT_IGNORE_LEADING_SPACE then
   popStyle.excludeStyle(POP_IGNORE_LEADING_SPACE) will return
   PopStyle.SHORT. To exclude all the leading styles use 
   popStyle.excludeLeadingStyles(). This is useful if you need to pop a sequence
   of tokens but only the first pop should skip spaces. For example:

   TeXObject firstObj = popStack(popStyle);
   popStyle = popStyle.excludeLeadingStyles();
   TeXObject nextObj = popStack(popStyle);
   
   When the parser has run out of pending objects it will fetch the
   next object from the current input reader. If it reaches the end of the
   file it will close the current reader and return to the parent
   reader (if there is one) otherwise it will throw EOFException.
  
   If a new file must be opened (e.g. with \input) then the parser
   will store any pending objects (that follow the input command)
   and open a new reader with the current reader as its parent. Once the new reader
   has closed the pending objects will be processed.
  
   In most cases, it's simpler to make process(TeXParser) call
   process(TeXParser,TeXObjectList) with the parser as the local
   stack. (Similarly for the Expandable methods.) However, if you
   then need to process another object, you'll need to check which
   process method to call. For example, an implementation of
   \@firstoftwo could be written as:

   public void process(TeXParser parser) throws IOException
   {
      TeXObject firstObj = parser.popRequired();
      TeXObject secondObj = parser.popRequired();

      firstObj.process(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXObject firstObj = parser.popRequired(stack);
      TeXObject secondObj = parser.popRequired(stack);

      firstObj.process(parser, stack);
   }

Alternatively:

   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXObject firstObj = parser.popRequired(stack);
      TeXObject secondObj = parser.popRequired(stack);

      if (parser == stack)
      {
         firstObj.process(parser);
      }
      else
      {
         firstObj.process(parser, stack);
      }
   }

   Note the difference between parser.popRequired() where the parser
   is the stack and parser.popRequired(stack) which does
   parser.popRequired() if stack == parser or stack == null or the
   stack is empty, otherwise it pops from the given stack instead of the
   parser. The TeXParser class provides a convenient method that determines
   which process method to use:

   parser.processObject(TeXObject object, TeXObjectList stack) so the
   above conditional can simply be replaced with:

   parser.processObject(firstObj, stack);
 
   The following methods can be used to pop objects where parser is the
   TeXParser argument, stack is the TeXObjectList argument and
   popStyle is the pop style:
 
   Tokens (these may be an AbstractGroup if one has been popped off
   earlier and pushed back onto the stack):
 
    parser.popNextToken(stack,popStyle)
    parser.popNextToken(stack)

     Pops a single object. The method without popStyle 
     uses PopStyle.DEFAULT

    parser.popNextTokenResolveReference(stack,popStyle)
    parser.popNextTokenResolveReference(stack)

      Like popNextToken(stack,popStyle) but also resolves the popped object 
      (using resolveReference). The method without popStyle uses
      PopStyle.DEFAULT

    parser.popNextTokenExpandOnce(stack,popStyle)
    parser.popNextTokenExpandOnce(stack)

     Pops single object after one expansion at the start of the stack 
     (if it can be expanded) using TeXParser.expandOnce(stack). The method 
     without popStyle uses PopStyle.DEFAULT

    parser.popNextTokenExpandFully(stack,popStyle)
    parser.popNextTokenExpandFully(stack)

     Pop single object after fully expanding the start of the stack (if it can 
     be expanded) using TeXParser.expandFully(stack). The method without 
     popStyle uses PopStyle.DEFAULT

  Arguments which may be delimited:
 
    parser.popRequired(stack,popStyle)
    parser.popRequired(stack)

     Pops either a single token or an argument delimited by group markers 
     (removes grouping). The method without popStyle uses 
     PopStyle.IGNORE_LEADING_SPACE, which emulates the way you can have spaces 
     between arguments. For example, \frac {1} {2} has two arguments with 
     spaces that should be ignored: the first space follows a control word
     (\frac) and so TeXParser represents it with a SkippedSpaces object
     (a sub-class of Ignoreable), but the second space is represented
     by a Space object (a sub-class of WhiteSpace), which needs to be skipped
     when popping an argument.

     This method is the most convenient way of popping a required 
     argument that may or may not be grouped. For example, to implement
     \@gobbletwo:

     public void process(TeXParser parser) throws IOException
     {
        process(parser, parser);
     }

     public void process(TeXParser parser, TeXObjectList stack) throws IOException
     {
        parser.popRequired(stack);
        parser.popRequired(stack);
     }

     This will work for \@gobbletwo{1}{2} and also for \@gobbletwo 12

    parser.popOptional(stack,popStyle)
    parser.popOptional(stack)

     Pops optional delimited argument (returns null if not found) with the 
     delimiters removed. The method without popStyle uses 
     PopStyle.IGNORE_LEADING_SPACE.

     The characters used for the delimiters are provided by 
     TeXParserListener methods getOptionalStartDelim and getOptionalEndDelim
     ('[' and ']' for the LaTeXParserListener).

    For example, to implement the command \newcommand{\foo}[1][Default Foo]{#1}

     public void process(TeXParser parser) throws IOException
     {
        process(parser, parser);
     }

     public void process(TeXParser parser, TeXObjectList stack) throws IOException
     {
        TeXObject optArg = parser.popOptional(stack);

        if (optArg == null)
        {
           optArg = parser.getListener().createString("Default Foo");
        }

        parser.processObject(optArg, stack);
     }

    parser.popRequiredExpandOnce(stack,popStyle)
    parser.popRequiredExpandOnce(stack)

      Like popRequired but also expands the popped object once (if it 
      can be expanded) using TeXParser.expandOnce(TeXObject,TeXObjectList)

    parser.popRequiredExpandFully(stack,popStyle)
    parser.popRequiredExpandFully(stack)

      Like popRequiredExpandOnce but expands fully (if it can be expanded)

    parser.popOptionalExpandOnce(stack,popStyle)
    parser.popOptionalExpandOnce(stack)

     Like popOptional but also expands once (if it can be expanded)

    parser.popOptionalExpandFully(stack,popStyle)
    parser.popOptionalExpandFully(stack)

     Like popOptional but also expands fully (if it can be expanded)

  Popping particular types of object:
 
    parser.popRequiredNumerical(stack,popStyle) 
    parser.popRequiredNumerical(stack) 

     Pops required numerical argument (which may be grouped, in which case the 
     grouping is removed, as per popRequired(stack)). Returns Numerical object.
     The method without popStyle uses PopStyle.SHORT_IGNORE_LEADING_SPACE.

    parser.popRequiredRegister(stack,popStyle)
    parser.popRequiredRegister(stack)

     Pops required register (not grouped) expanding the start of the stack. 
     Returns a Register object. 
     The method without popStyle uses PopStyle.SHORT_IGNORE_LEADING_SPACE.

     For example, \the\myreg is valid (assuming \myreg has been defined) but 
     \the{\myreg} isn't valid. An implementation of \the would need
     to use popRequiredRegister.

    parser.popRequiredDimension(stack,popStyle) 
    parser.popRequiredDimension(stack) 

     Pops required dimension (not grouped) expanding the start of the stack. 
     Returns a TeXDimension object.
     The method without popStyle uses PopStyle.SHORT_IGNORE_LEADING_SPACE.

    parser.popRequiredFloat(stack,popStyle)
    parser.popRequiredFloat(stack)

     Pops required Float (not grouped) expanding the start of the stack.
     Returns a Float object.
     The method without popStyle uses PopStyle.SHORT_IGNORE_LEADING_SPACE.

    parser.popRequiredNumber(stack,popStyle)
    parser.popRequiredNumber(stack)

     Pops required TeXNumber (no grouped) expanding the start of the stack.
     The number may be in one of the following forms:

       - "<hex> where <hex> is a hexadecimal number (e.g. "4B).
       - '<octal> where <octal> is an octal number (e.g. '113).
       - `\<c> or `<c> where <c> is a character (e.g. `\K or `K) trailing spaces
         are discarded (the numeric value is the character code).
       - <num> where <num> is an integer (e.g. 75).
       - a TeXNumber object (for example, an object such as a
         CountRegister or UserNumber that has been
         pushed onto the stack)

    parser.popRequiredControlSequence(stack,resolve,popStyle)
    parser.popRequiredControlSequence(stack,popStyle)
    parser.popRequiredControlSequence(stack,resolve)
    parser.popRequiredControlSequence(stack)

       Pops required ControlSequence (which may be grouped, in which case 
       the grouping is stripped). Returns a ControlSequence object.

       The methods without popStyle uses PopStyle.SHORT_IGNORE_LEADING_SPACE.

       If resolve==true then the control sequence is resolved before returning. 
       If TeXCsRef is popped off the stack then the control sequence is
       obtained from the TeXParserListener.getControlSequence(String) method.
       If the ControlSequence is an instance of an AssignedMacro and the base underlying
       object is a ControlSequence then that's returned otherwise the
       original ControlSequence or the ControlSequence obtained from resolving
       TeXCsRef is returned. (When the parser reads a control sequence in from
       the input reader, it's represented with TeXCsRef until it's time to 
       expand or process it.)

    parser.popRequiredString(stack,popStyle)
    parser.popRequiredString(stack)

    Just does popRequiredExpandFully(stack,popStyle).stripToString(parser)
    which pops an argument, expands it fully and then strips all 
    non-character content (ignoreables, macros that the library doesn't
    know how to expand etc). Returns String object.

    parser.popOptionalString(stack,popStyle)
    parser.popOptionalString(stack)

    As popRequiredString but for optional arguments.


    boolean parser.isNextChar(int codePoint, TeXObjectList stack) 
    boolean parser.isNextChar(int codePoint, TeXObjectList stack, byte popStyle) 
      Uses peekStack(popStyle) to test if the next object is a CharObject. If it
      is and the character code == codePoint, then that object is popped off
      the stack and true is returned. Otherwise the object is left on the stack
      and false is return. Note that even if false is returned, leading objects
      may be lost if the popStyle requires them to be skipped.

      The method without popStyle uses PopStyle.SHORT_IGNORE_LEADING_SPACE.

      For example, \@ifstar could be implemented as

     public void process(TeXParser parser) throws IOException
     {
        process(parser, parser);
     }

     public void process(TeXParser parser, TeXObjectList stack) throws IOException
     {
        boolean isStar = parser.isNextChar('*', stack);
        TeXObject firstArg = parser.popRequired(stack);
        TeXObject secondArg = parser.popRequired(stack);

        if (isStar)
        {
           parser.processObject(firstArg, stack);
        }
        else
        {
           parser.processObject(secondArg, stack);
        }
     }

     boolean parser.isNextWord(String word, TeXObjectList stack)
     boolean parser.isNextWord(String word, TeXObjectList stack, PopStyle popStyle)

     Similar to isNextChar this tests if the stack starts with the given
     sequence of characters. If the complete word is found, the characters are 
     popped off the stack and true is returned otherwise false is returned.
     Note that even if false is returned, leading objects
     may be lost if the popStyle requires them to be skipped.
     This method doesn't allow for the word to be enclosed in a group.

 Additional methods that don't pop:

    boolean parser.isNextObject(Class objectClass,TeXObjectList stack,byte popStyle)
    boolean parser.isNextObject(Class objectClass,TeXObjectList stack) 

    Tests if the next token is an object of the given class. 

    Unlike isNextChar the token isn't popped off the stack and the method will 
    return false if the first object is a group.

    boolean isCharacter(TeXObject object, int... codePoints)

    Tests if object is a CharObject with one of the given character codepoints.
    This will also allow for the case where object is an instance of Group
    containing a single element. (You may have to trim it first to remove
    unwanted leading and trailing spaces or ignoreables.)

    int parser.toCodePoint(TeXObject object)

    If the given object is a CharObject or an AbstractTeXObjectList containing
    a single element that's a CharObject that object is returned otherwise
    null is returned.

    boolean parser.isControlSequenceTrue(String csname)

    This uses TeXParser.getControlSequence(csname) (not the listener's method)
    to fetch the ControlSequence with the given name. If that control sequence
    represents a condition (e.g. \iftrue) then it returns the boolean value
    otherwise it returns false. For example, isControlSequenceTrue("iftrue")
    returns true and isControlSequenceTrue("iffalse") returns false. The
    conditional must have first been defined.

    boolean isTrue(TeXObject object)

    Tests if an object is true. If the object is null, returns
    false. If the object is an instance of TeXBoolean the value is
    obtained from TeXBoolean.booleanValue(). If the object is an AssignedMacro
    the test is performed on the base underlying object. Otherwise false is
    returned.

    TeXObject resolveReference(TeXObject)

    If the given TeXObject is a reference (TeXCsRef) or an AssignedMacro
    returns the referenced underyling object otherwise it returns the 
    original object.

    TeXObject expandOnce(TeXObject object, TeXObjectList stack) 

    If object can be expanded, return the result from expanding it once
    otherwise return the original object.

    TeXObject expandFully(TeXObject object, TeXObjectList stack) 

    If object can be expanded, return the result from expanding it fully
    otherwise return the original object.

Stacks vs Other Lists

    The TeXObjectList stack is a sub-class of AbstractTeXObjectList and 
    inherits or overrides methods from there. AbstractGroup is also a sub-class
    of AbstractTeXObjectList so objects can be popped off groups (without
    removing the group markers) but an AbstractGroup can't be used as a stack.
    An AbstractGroup can be deconstructed into a stack that contains the 
    start and end group markers.

    There are some additional methods provided by AbstractTeXObjectList or 
    TeXObjectList that are inherited by TeXParser:

    AbstractTeXObjectList.popToCsMarker(TeXParser,String,popStyle) 
     pops objects up to the control sequence with the name given by the
     String argument. This will only expand objects if ExpandAfter is
     encountered. Returns a stack that can be pushed to the start of
     another list with AbstractTeXObjectList.push(TeXObject) or it can
     be fully expanded with TeXObjectList.expandfully().
   
    The peek methods allow you to inspect the next object on the
stack. Again the pop style determines whether or not to ignore any
leading whitespace or ignoreable content. 

    TeXObjectList.peekStack(byte) - returns null if the stack is empty
    TeXObjectList.peekStack() - just calls peekStack(PopStyle.DEFAULT)

    TeXObjectList.peek() is analogous to pop(), it will return the first element regardless of its class (or null if the stack is empty).
    Similarly TeXObjectList.peekLast() will return the last element (or null) regardless of the element's class.

 */

public interface TeXObject extends Cloneable
{
   /*
    Process this.
   */
   public void process(TeXParser parser)
      throws IOException;

   /*
    Process this with a local stack.

   */
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException;

   /* As above but stop processing and return true if given marker is found.
    * Typically this should only affect list processing.
    */
   public boolean process(TeXParser parser, TeXObjectList stack, StackMarker marker)
      throws IOException;

   public Object clone();

   public String toString(TeXParser parser);

   /* Gets a string version of this with any non-writeable characters (e.g. 
    * unexpandable macros) removed. Active characters may be detokenized if appropriate. 
    * Returns empty string if no writeable content.
   */ 
   public String stripToString(TeXParser parser)
    throws IOException;

   public TeXObjectList string(TeXParser parser)
    throws IOException;

   public String format();

   public boolean isPar();

   /* NB an empty MathGroup isn't an empty object */
   public boolean isEmptyObject();

   /* If true popStack and popToken should skip this object */
   public boolean isPopStyleSkip(PopStyle popStyle);

   public int getTeXCategory();

   /*
    * TeXObject that doesn't represent any character.
    */ 

   public static final int TYPE_OBJECT=-1;

   /*
    * Standard TeX categories. 
    */  
   /** Escape character. */
   public static final int TYPE_ESC = 0;
   /** Begin-group character. */
   public static final int TYPE_BG=1;
   /** End-group character. */
   public static final int TYPE_EG=2;
   /** Math-shift character. */
   public static final int TYPE_MATH=3;
   /** Alignment tab character. */
   public static final int TYPE_TAB=4;
   /** End of line character. */
   public static final int TYPE_EOL=5;
   /** Parameter character. */
   public static final int TYPE_PARAM=6;
   /** Superscript character. */
   public static final int TYPE_SP=7;
   /** Subscript character. */
   public static final int TYPE_SB=8;
   /** Ignored character. */
   public static final int TYPE_IGNORE=9;
   /** Space character. */
   public static final int TYPE_SPACE=10;
   /** Letter. */
   public static final int TYPE_LETTER=11;
   /** Other. */
   public static final int TYPE_OTHER=12;
   /** Active character. */
   public static final int TYPE_ACTIVE=13;
   /** Comment character. */
   public static final int TYPE_COMMENT=14;
   /** Invalid character. */
   public static final int TYPE_INVALID=15;

   /*
    * Pop styles.
    */ 

   public static byte POP_DEFAULT=0;
   public static byte POP_SHORT=1;
   public static byte POP_RETAIN_IGNOREABLES=2;
   public static byte POP_IGNORE_LEADING_SPACE=4;
   public static byte POP_IGNORE_LEADING_PAR=8;
}

