# TeX Java Parser Library

I am in the process of working on a way to get more of my
(La)TeX-related Java applications into TeX Live. This will make it
easier for people to install the applications (in the usual way via
the TL package manager), but it means making changes in order to
comply with TL requirements.

The library is being renamed the TeX Java Parser Library (instead of
just the TeX Parser Library). This was suggested by Karl Berry to
fit in with the [TeX Java Help Library](https://github.com/nlct/texjavahelp)
naming scheme, and it also highlights that it's a Java library. 

The jar file will be renamed `texjavaparserlib.jar`
(from `texparserlib.jar`). The package name
`com.dickimawbooks.texparserlib` is unchanged. Renaming the jar file
will help prevent any conflict with the `texparserlib.jar` file currently
bundled with [`bib2gls`](https://github.com/nlct/bib2gls).

The aim is to have the libraries distributed separately and create a
script that locates the required libraries in `texmfscripts` and
adds them to the class path when running the application that
depends on them.  This will avoid having multiple copies of
`texparserlib.jar` (and `texjavahelplib.jar`) in the TeX
distribution (which TL doesn't allow).  It will also allow
`texjavahelp.sty` into TL, which makes it easier to build the
application documentation from the source code. 

## BACKGROUND

In the past, I created a Java application
[MakeJmlrBookGUI](http://www.dickimaw-books.com/software/makejmlrbookgui/)
that needed to parse LaTeX files so that it could fix common problems. This
mainly involved replacing obsolete/problematic code and removing
`.eps` from included graphics, so that the book correctly compiled
with `pdflatex` and my [`jmlrbook` class](http://ctan.org/pkg/jmlr).
(This no longer works as the underlying required class has stopped
working following changes to the LaTeX kernel. The `jmlrbook` class is
too fragile to continue to support, and the group who asked me to
create it no longer need it.)

Unfortunately TeX syntax can be too complex for a regular
expression. The TeX Parser library is not intended as a TeX
engine, but as a way of parsing TeX code that's somewhat better than
a simple pattern match.

When TeX4HT stopped worked with the `jmlrbook` class (before the
class itself stopped working with new LaTeX kernels), I started to
extend the TeX parsing code so that it could convert the article
abstracts to HTML without requiring TeX4HT or other LaTeX to HTML
systems that may have similar problems. This aspect is no longer
required as PLMR (formerly JMLR W&amp;CP) now generate the HTML files from the
`.bib` file associated with the proceedings, but the `html` part of
the TeX parser library allows the translation of code fragments,
such as author name or article title, so it can be rendered in
Java's `HTMLDocument`, which makes the GUI look a bit tidier.

In addition to parsing the LaTeX source code,
the `.aux` files also needed parsing to pick out various bits of
information, and the `.bib` files supplied by contributing authors
needed to have just the actual referenced entries extracted.
So the library includes code to gather information from `.aux` and
`.bib` files.

Since I have other Java applications (for example,
[`datatooltk`](https://github.com/nlct/datatooltk) and
[`bib2gls`](https://github.com/nlct/bib2gls)) that also need to
parse LaTeX files or their associated `.aux` or `.bib` files, I
decided to split away the TeX parsing code from MakeJmlBookGui into
a separate library, namely `texparserlib.jar` (now renamed
`texjavaparserlib.jar`). This also makes it easier to test the
library without the additional overhead of the main program.

There are only a few LaTeX packages implemented and some of them
aren't a full implementation. These are provided as some aspects of
them are required for the applications using the TeX Parser
library. For example, MakeJmlrBookGUI needed to convert articles that
use the old `jmlr2e` package so that they instead use the new `jmlr`
class, the `datatooltk` application can import `probsoln` data sets, and
`bib2gls` needs to know symbols that commonly occur in glossary
entries. So, for example, `texparserlib.jar` recognises `siunitx`'s
`\si{}` command as it's feasible that a user might want to define units used
in the document, but it's less likely that a specific measurement
might occur in the `name` field (although measurements may well
occur in the `description` when defining constants, but that's less
important to `bib2gls` since entries aren't usually sorted by their
description).

The accompanying `texparsertest.jar` is a command line application
provided to test the TeX Parser library. It's not intended for
general use, although I do use it to create the HTML version of my
package documentation. (It's very slow but it's a useful way of
testing the system.) For example, `glossaries-user.html` is an HTML
alternative to `glossaries-user.pdf` that's created with
`texparsertest.jar`.

Syntax:

`texparsertest` [`--html`] `--in` &lt;*tex file*&gt; `--output` &lt;*out dir*&gt;

This parses &lt;*tex file*&gt; and saves the new file in &lt;*out dir*&gt; and
copies over any included images. It will run `epstopdf` on any eps
files and `wmf2eps` on any wps files. Both `epstopdf` and `wmf2eps` must
be on your system path. The `--html` switch indicates conversion to
HTML. If this switch is omitted, LaTeX to LaTeX conversion is
assumed, which will produce a single flattened file (that is, instances of
`\input` will be replaced with the referenced file's content).

I experimented with including a GUI to provide a way of testing the
library with a graphical interface, but I've now removed it as I don't have time
to develop it, and it requires additional libraries. There's still
some legacy content from that which needs removing.

The [TeX Java Help System](https://github.com/nlct/texjavahelp) uses
the TeX Parser Library.  The command line `texjavahelpmk.jar`
application is similar to `texparsertest --html` but is customized
to work with the `texjavahelplib.jar` library and has added support
for `texjavahelp.sty`. This is used with `flowframtk` and
`datatooltk` to provide the in-application manual created from the
LaTeX source and will also be used with future versions of other
applications, such as `makeglossariesgui`.
The command line `tjhflattendocsrc.jar` application is similar to
using `texparsertest` in LaTeX-to-LaTeX mode.

## TEST FILES


Test files are in `src/tests/`

### Example 1:

The test file `src/tests/test-obsolete/test-obs.tex` contains obsolete
commands such as `\bf`, `\centerline` and `\epsfig`.
```bash
cd src/tests
texparsertest --in test-obsolete/test-obs.tex --output output/test-obsolete
```
This will create the directory `output/test-obsolete` and create a
file in it called `test-obs.tex` which is the original file with the
obsolete commands replaced. The eps file is also copied over to the
new directory and `epstopdf` is used to create a corresponding pdf
file. The `\epsfig` command is converted to `\includegraphics` with the
file extension removed.

Font changing commands, such as `\bf`, are changed to the
corresponding LaTeX2e font declarations (such as `\bfseries`) in text mode
and the corresponding LaTeX2e math font commands (such as `\mathbf`)
in math mode. The commands are unchanged if they occur in the
argument of `\verb` or in a command definition.

### Example 2:

The test file `src/tests/test-sw/test-sw.tex` simulates output from
Scientific Word. (I don't have SW so I can't test this. The code is
based on the type of code I've had to work with as a production
editor.) In general I don't want commands such as `\bigskip` in the
articles, as the inter-paragraph spacing should be dependent on the 
book style. Also, I don't have `tcilatex.tex` used by SW, so
`texparserapp` removes `\input{tcilatex}` and substitutes `\FRAME` and
`\Qcb`. I don't know what other commands `tcilatex` defines. Those two
are the only ones I've encountered so far. `texparserapp` also
replaces
`\special{language "Scientific Word";...;tempfilename '`*imgname*`.wmf'}` with `\includegraphics{`*imgname*`}` and runs `wmf2eps` on
the wmf image.
```bash
cd src/tests
texparsertest --in test-sw/test-sw.tex --output output/test-sw
```
This creates the directory `output/test-sw` and writes a copy of
`test-sw.tex` with the relevant substitutions. The image file
`X0001.wmf` is converted to eps and the eps file is then converted to
pdf.

### Example 3:

Conversion to HTML just creates a single HTML file and copies over image files.
It's very limited as I initially only needed to convert
abstracts to HTML. MathJax is used to render math mode.
```bash
cd src/tests
texparsertest --in test-article/test-article.tex --output output/test-article --html
```
The `bib2gls` application uses the HTML conversion without MathJax
when trying to interpret the sort value when the `sort` field is
missing, so this test file now includes some packages that have been
added to help `bib2gls`. These are mostly packages that provide
symbols that might appear in a glossary. Some support for `datatool` has also
been added to assist `datatooltk`.

Support for the `glossaries` and `glossaries-extra` packages is
provided for `texjavahelpmk` (not for `bib2gls`, which has its own
implementation of glossary commands that may occur in entry fields).

I would like at some point to develop the
`com.dickimawbooks.texparserlib.image` package for the benefit of
the `jdr.jar` library (used by `flowframtk`) but this would be a
major undertaking as there are many commands that would need
implementing.

And, of course, all the libraries will need documenting.
