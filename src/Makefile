ifeq ($(shell javac -version 2>&1 | grep 1.8),)
# Java 8 extended support until December 2030
  target_version := --release 8 -Xlint:-options
else
  target_version = 
endif

test	: test-obs test-abs test-article test-nested test-doc \
	  test-probsoln test-lists test-datatool test-mkimages \
	  test-jmlrutils test-glossaries test-glossaries-extra \
	  test-fontawesome test-userguide test-input test-datatool-v3 \
	  test-article-split test-graphics

tests/output	:
	mkdir -p tests/output

test-nested	: lib/texjavaparsertest.jar tests/output
	rm -f -r tests/output/test-nested-env-group
	bin/texjavaparsertest --debug --in tests/test-nested-env-group/test-nested-env-group.tex --output tests/output/test-nested-env-group

test-obs	: lib/texjavaparsertest.jar tests/output
	rm -f -r tests/output/test-obsolete
	bin/texjavaparsertest --debug --log tests/output/test-obsolete.log --in tests/test-obsolete/test-obs.tex --output tests/output/test-obsolete
	cd tests/output/test-obsolete; pdflatex test-obs.tex

test-sw	: lib/texjavaparsertest.jar tests/output
	rm -f -r tests/output/test-sw
	bin/texjavaparsertest --debug --in tests/test-sw/test-sw.tex --output tests/output/test-sw

test-abs : lib/texjavaparsertest.jar tests/output
	rm -f -r tests/output/test-abs
	bin/texjavaparsertest --debug --in tests/test-abs/test-abs.tex --output tests/output/test-abs --html

test-article : lib/texjavaparsertest.jar tests/test-article/test-article.pdf tests/output
	rm -f -r tests/output/test-article
	bin/texjavaparsertest --debug --in tests/test-article/test-article.tex --output tests/output/test-article --html

tests/test-article/test-article.pdf	: tests/test-article/test-article.tex
					arara -w tests/test-article test-article

test-article-split : lib/texjavaparsertest.jar tests/test-article/test-article.pdf tests/output
	rm -f -r tests/output/test-article-split
	bin/texjavaparsertest --debug --in tests/test-article/test-article.tex --output tests/output/test-article-split --html --split 1

test-input : lib/texjavaparsertest.jar tests/test-input/test-input-main.pdf tests/output
	rm -f -r tests/output/test-input
	bin/texjavaparsertest --debug --log tests/output/test-input.log --in tests/test-input/test-input-main.tex --output tests/output/test-input --html

tests/test-input/test-input-main.pdf	: tests/test-input/test-input-main.tex
					arara -w tests/test-input test-input-main

test-glossaries : lib/texjavaparsertest.jar tests/test-glossaries/test-glossaries.pdf tests/output
	rm -f -r tests/output/test-glossaries
	bin/texjavaparsertest --debug --in tests/test-glossaries/test-glossaries.tex --output tests/output/test-glossaries --html

tests/test-glossaries/test-glossaries.pdf	: tests/test-glossaries/test-glossaries.tex
					arara -w tests/test-glossaries test-glossaries

test-glossaries-extra : lib/texjavaparsertest.jar tests/test-glossaries-extra/test-glossaries-extra.pdf tests/output
	rm -f -r tests/output/test-glossaries-extra
	bin/texjavaparsertest --debug --in tests/test-glossaries-extra/test-glossaries-extra.tex --output tests/output/test-glossaries-extra --html

tests/test-glossaries-extra/test-glossaries-extra.pdf	: tests/test-glossaries-extra/test-glossaries-extra.tex
					arara -w tests/test-glossaries-extra test-glossaries-extra

test-fontawesome : lib/texjavaparsertest.jar tests/test-fontawesome/test-fontawesome.pdf tests/output
	rm -f -r tests/output/test-fontawesome
	bin/texjavaparsertest --debug --in tests/test-fontawesome/test-fontawesome.tex --output tests/output/test-fontawesome --html

tests/test-fontawesome/test-fontawesome.pdf	: tests/test-fontawesome/test-fontawesome.tex
					arara -w tests/test-fontawesome test-fontawesome

test-userguide : lib/texjavaparsertest.jar tests/test-userguide/test-userguide.pdf tests/output
	rm -f -r tests/output/test-userguide
	bin/texjavaparsertest --no-convert-images --debug-mode process-generic-cs,expansion,process,process-stack,io,settings,popped --log tests/output/test-userguide.log --in tests/test-userguide/test-userguide.tex --output tests/output/test-userguide --html

tests/test-userguide/test-userguide.pdf	: tests/test-userguide/test-userguide.tex
					arara -w tests/test-userguide test-userguide

test-jmlrutils :lib/texjavaparsertest.jar  tests/output
	rm -f -r tests/output/test-jmlrutils
	bin/texjavaparsertest --debug --in tests/test-jmlrutils/test-jmlrutils.tex --output tests/output/test-jmlrutils --html

test-doc : lib/texjavaparsertest.jar tests/output
	rm -f -r tests/output/test-doc
	bin/texjavaparsertest --in tests/test-doc/test-doc.tex --output tests/output/test-doc --html

test-probsoln : lib/texjavaparsertest.jar tests/output
	rm -f -r tests/output/test-probsoln
	bin/texjavaparsertest --debug --in tests/test-probsoln/test-probsoln.tex --output tests/output/test-probsoln --html

test-lists : lib/texjavaparsertest.jar tests/output
	rm -f -r tests/output/test-lists
	bin/texjavaparsertest --in tests/test-lists/test-lists.tex --output tests/output/test-lists --html

test-datatool : lib/texjavaparsertest.jar tests/output
	rm -f -r tests/output/test-datatool
	bin/texjavaparsertest --debug --in tests/test-datatool/test-datatool.tex --output tests/output/test-datatool --html

test-datatool-v3 : lib/texjavaparsertest.jar tests/output
	rm -f -r tests/output/test-datatool-v3
	bin/texjavaparsertest --debug-mode process-generic-cs,expansion,process,process-stack,io --log tests/output/test-datatool-v3.log --in tests/test-datatool-v3/test-datatool-v3.tex --output tests/output/test-datatool-v3 --html

test-jmlr	: lib/texjavaparsertest.jar tests/output
	rm -f -r tests/output/test-jmlr
	bin/texjavaparsertest --in tests/test-jmlr/test-jmlr.tex --output tests/output/test-jmlr

test-mkimages : lib/texjavaparsertest.jar tests/output
	rm -f -r tests/output/test-mkimages
	bin/texjavaparsertest --debug --in tests/test-mkimages/test-mkimages.tex --output tests/output/test-mkimages --html

test-graphics	: lib/texjavaparsertest.jar tests/output
	rm -f -r tests/output/test-graphics
	bin/texjavaparsertest --debug --image-dest images --in tests/test-graphics/test-graphics.tex --output tests/output/test-graphics

lib/texjavaparserlib.jar      : classes/com/dickimawbooks/texparserlib/TeXParser.class 
	cd classes; \
	jar cf ../lib/texjavaparserlib.jar \
	com/dickimawbooks/texparserlib/*.class \
	com/dickimawbooks/texparserlib/*/*.class \
	com/dickimawbooks/texparserlib/*/*/*.class 

lib/texjavaparsertest.jar      : java/test/Manifest.txt \
			classes/com/dickimawbooks/texparsertest/TeXParserApp.class 
	cd classes; \
	jar cmf ../java/test/Manifest.txt ../lib/texjavaparsertest.jar \
	com/dickimawbooks/texparsertest/*.class \
	com/dickimawbooks/texparsertest/*/*.class 

classes/com/dickimawbooks/texparsertest/TeXParserApp.class   : classes/com/dickimawbooks/texjavaparsertest \
	java/test/*.java java/test/*/*.java \
	lib/texjavaparserlib.jar
	cd java/test; \
	javac -d ../../classes \
	 -Xlint:unchecked -Xlint:deprecation \
	-cp ../../lib/texjavaparserlib.jar \
	*.java */*.java

classes/com/dickimawbooks/texparserlib/TeXParser.class   : classes/com/dickimawbooks/texparserlib \
	java/lib/*.java java/lib/*/*.java java/lib/*/*/*.java
	cd java/lib; \
	javac $(target_version) -d ../../classes -Xlint:unchecked -Xlint:deprecation *.java */*.java */*/*.java

classes/com/dickimawbooks/texjavaparsertest    :
	mkdir -p classes/com/dickimawbooks/texjavaparsertest

classes/com/dickimawbooks/texparserlib    :
	mkdir -p classes/com/dickimawbooks/texparserlib

source	:
	cd ..; git archive -o texjavaparser-src.zip HEAD; \
	mkdir -p texjavaparser-src; \
	unzip -d texjavaparser-src texjavaparser-src.zip; \
	rm texjavaparser-src.zip; \
	find texjavaparser-src -name .gitignore -exec rm {} \; ;\
	zip -r texjavaparser-src.zip texjavaparser-src; \
	rm -r texjavaparser-src; mv texjavaparser-src.zip dist


clean   :
	\rm -f classes/com/dickimawbooks/texparserlib/*.class
	\rm -f classes/com/dickimawbooks/texparsertest/*.class

squeaky :
	\rm -f lib/texjavaparsertest.jar
	\rm -f lib/texjavaparserlib.jar

