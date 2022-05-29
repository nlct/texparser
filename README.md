# TeX Parser Library

This code is *highly experimental* and still under construction. It
comes with no guarantees, no warranties and is not future-proof.
Note that version 0.8b has some file name changes. The most
important one being the renaming of the former `aux` directory
(see issue #1).

## PURPOSE

My Java application
[MakeJmlrBookGUI](http://www.dickimaw-books.com/software/makejmlrbookgui/)
needs to parse LaTeX files so that it can fix common problems. This
mainly involves replacing obsolete/problematic code and removing
`.eps` from included graphics, so that the book correctly compiles
with `pdflatex` and my [`jmlrbook` class](http://ctan.org/pkg/jmlr).
Unfortunately TeX syntax can be too complex for a regular
expression. The `texparserlib.jar` library is not intended as a TeX
engine, but as a way of parsing TeX code that's somewhat better than
a simple pattern match.

Since TeX4HT no longer works with the `jmlrbook` class, I started to
extend the TeX parsing code so that it could convert the article
abstracts to HTML without requiring TeX4HT. This aspect is no
longer required as JMLR W&amp;CP now generate the HTML files from the
`.bib` file associated with the proceedings, but the `html` part of the
TeX parser library allows the translation of code fragments, such as 
author name or article title, so it can be rendered in Java's
`HTMLDocument`, which makes the GUI look a bit tidier.

Since I have other Java applications (for example,
[`datatooltk`](https://github.com/nlct/datatooltk) and
[`bib2gls`](https://github.com/nlct/bib2gls)) that also need to
parse LaTeX files or their associated `.aux` or `.bib` files, I
decided to split away the TeX parsing code from MakeJmlBookGui into
a separate library, namely `texparserlib.jar`. This also makes it
easier to test the library without the additional overhead of the
main program.

There are only a few LaTeX packages implemented and some of them
aren't a full implementation. These are provided as some aspects of
them are required for the applications using the `texparserlib.jar`
library. For example, MakeJmlrBookGUI needs to convert articles that
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
provided to test the `texparserlib.jar` library. It's not intended for
general use. (For this reason, I've renamed it from
`texparserapp.jar` to `texparsertest.jar`. There is still a script
called `texparserapp` in the `bin` directory which does the same
thing as `texparsertest`.)

Syntax:

`texparsertest` [`--html`] `--in` &lt;*tex file*&gt; `--output` &lt;*out dir*&gt;

This parses &lt;*tex file*&gt; and saves the new file in &lt;*out dir*&gt; and
copies over any included images. It will run `epstopdf` on any eps
files and `wmf2eps` on any wps files. Both `epstopdf` and `wmf2eps` must
be on your system path. The `--html` switch indicates conversion to
HTML. If this switch is omitted, LaTeX to LaTeX conversion is
assumed.

The output directory &lt;*out dir*&gt; must not exist. This is a
precautionary measure to ensure you don't accidentally overwrite the
original files.

I experimented with including a GUI to provide a way of testing the
library with a graphical interface but I've now removed it as I don't have time
to develop it, and it requires additional libraries.

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

Conversion to HTML just creates a single HTML file.
It's very limited as I initially only needed to convert
abstracts to HTML. Images aren't supported. MathJax is used to 
render math mode.
```bash
cd src/tests
texparsertest --in test-article/test-article.tex --output output/test-article --html
```
The `bib2gls` application uses the HTML conversion without MathJax
when trying to interpret the sort value when the `sort` field is
missing, so this test file now includes some packages that have been
added to help `bib2gls`. These are mostly packages that provide
symbols that might appear in a glossary.
