/**
 * Provides the classes to parse (La)TeX source.
 * The principle class is the {@link com.dickimawbooks.texparserlib.TeXParser} 
 * class, which needs a listener that implements 
 * {@link com.dickimawbooks.texparserlib.TeXParserListener}
 * and an application that implements
 * {@link com.dickimawbooks.texparserlib.TeXApp}
 *
 * TeX primitives are implemented by classes in the 
 * {@link com.dickimawbooks.texparserlib.primitives}
 * library.
 *
 * This library is mostly gear towards LaTeX but some plain TeX
 * support is provided with the
 * {@link com.dickimawbooks.texparserlib.plain}
 * library.
 *
 * The
 * {@link com.dickimawbooks.texparserlib.auxfile}
 * library may be used to gather information from an aux file
 * that was created by LaTeX.
 *
 * The
 * {@link com.dickimawbooks.texparserlib.bib}
 * library may be used to gather information from a bib file.
 *
 * The
 * {@link com.dickimawbooks.texparserlib.latex}
 * library provides an abstract listener for parsing LaTeX source
 * code and support for a limited set of common kernel commands and
 * very limited support for a small set of LaTeX packages.
 * This corresponds to packages which I have needed to provide some
 * support for.
 *
 * The
 * {@link com.dickimawbooks.texparserlib.latex.latex3}
 * library provides very limited support for LaTeX3.
 *
 * The 
 * {@link com.dickimawbooks.texparserlib.generic}
 * library forms the basis of the
 * {@link com.dickimawbooks.texparserlib.plain},
 * {@link com.dickimawbooks.texparserlib.auxfile},
 * {@link com.dickimawbooks.texparserlib.latex}, and
 * {@link com.dickimawbooks.texparserlib.bib} libraries.
 *
 * The 
 * {@link com.dickimawbooks.texparserlib.latex2latex} and
 * {@link com.dickimawbooks.texparserlib.html} libraries, build on the
 * {@link com.dickimawbooks.texparserlib.latex} library and
 * provide listeners that extend the abstract
 * {@link com.dickimawbooks.texparserlib.latex.LaTeXParserListener}
 * class.
 *
 */
package com.dickimawbooks.texparserlib;
