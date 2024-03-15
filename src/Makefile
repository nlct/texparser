ifeq ($(shell javac -version 2>&1 | grep 1.8),)
  target_version := --release 8
else
  target_version = 
endif

test	: test-obs test-abs test-article test-nested test-doc \
	  test-probsoln test-lists test-datatool test-mkimages \
	  test-jmlrutils test-glossaries test-glossaries-extra \
	  test-fontawesome test-userguide test-input test-datatool-v3 \
	  test-article-split

tests/output	:
	mkdir -p tests/output

test-nested	: app tests/output
	rm -f -r tests/output/test-nested-env-group
	bin/texparsertest --debug --in tests/test-nested-env-group/test-nested-env-group.tex --output tests/output/test-nested-env-group

test-obs	: app tests/output
	rm -f -r tests/output/test-obsolete
	bin/texparsertest --debug --log tests/output/test-obsolete.log --in tests/test-obsolete/test-obs.tex --output tests/output/test-obsolete
	cd tests/output/test-obsolete; pdflatex test-obs.tex

test-sw	: app tests/output
	rm -f -r tests/output/test-sw
	bin/texparsertest --debug --in tests/test-sw/test-sw.tex --output tests/output/test-sw

test-abs : app tests/output
	rm -f -r tests/output/test-abs
	bin/texparsertest --debug --in tests/test-abs/test-abs.tex --output tests/output/test-abs --html

test-article : app tests/test-article/test-article.pdf tests/output
	rm -f -r tests/output/test-article
	bin/texparsertest --debug --in tests/test-article/test-article.tex --output tests/output/test-article --html

tests/test-article/test-article.pdf	: tests/test-article/test-article.tex
					cd tests/test-article; arara test-article

test-article-split : app tests/test-article/test-article.pdf tests/output
	rm -f -r tests/output/test-article-split
	bin/texparsertest --debug --in tests/test-article/test-article.tex --output tests/output/test-article-split --html --split 1

test-input : app tests/test-input/test-input-main.pdf tests/output
	rm -f -r tests/output/test-input
	bin/texparsertest --debug --log tests/output/test-input.log --in tests/test-input/test-input-main.tex --output tests/output/test-input --html

tests/test-input/test-input-main.pdf	: tests/test-input/test-input-main.tex
					cd tests/test-input; arara test-input-main

test-glossaries : app tests/test-glossaries/test-glossaries.pdf tests/output
	rm -f -r tests/output/test-glossaries
	bin/texparsertest --debug --in tests/test-glossaries/test-glossaries.tex --output tests/output/test-glossaries --html

tests/test-glossaries/test-glossaries.pdf	: tests/test-glossaries/test-glossaries.tex
					cd tests/test-glossaries; arara test-glossaries

test-glossaries-extra : app tests/test-glossaries-extra/test-glossaries-extra.pdf tests/output
	rm -f -r tests/output/test-glossaries-extra
	bin/texparsertest --debug --in tests/test-glossaries-extra/test-glossaries-extra.tex --output tests/output/test-glossaries-extra --html

tests/test-glossaries-extra/test-glossaries-extra.pdf	: tests/test-glossaries-extra/test-glossaries-extra.tex
					cd tests/test-glossaries-extra; arara test-glossaries-extra

test-fontawesome : app tests/test-fontawesome/test-fontawesome.pdf tests/output
	rm -f -r tests/output/test-fontawesome
	bin/texparsertest --debug --in tests/test-fontawesome/test-fontawesome.tex --output tests/output/test-fontawesome --html

tests/test-fontawesome/test-fontawesome.pdf	: tests/test-fontawesome/test-fontawesome.tex
					cd tests/test-fontawesome; arara test-fontawesome

test-userguide : app tests/test-userguide/test-userguide.pdf tests/output
	rm -f -r tests/output/test-userguide
	bin/texparsertest --no-convert-images --debug-mode process-generic-cs,expansion,process,process-stack,io --log tests/output/test-userguide.log --in tests/test-userguide/test-userguide.tex --output tests/output/test-userguide --html

tests/test-userguide/test-userguide.pdf	: tests/test-userguide/test-userguide.tex
					cd tests/test-userguide; arara test-userguide

test-jmlrutils : app tests/output
	rm -f -r tests/output/test-jmlrutils
	bin/texparsertest --no-rm-tmp-dir --debug --in tests/test-jmlrutils/test-jmlrutils.tex --output tests/output/test-jmlrutils --html

test-doc : app tests/output
	rm -f -r tests/output/test-doc
	bin/texparsertest --in tests/test-doc/test-doc.tex --output tests/output/test-doc --html

test-probsoln : app tests/output
	rm -f -r tests/output/test-probsoln
	bin/texparsertest --debug --in tests/test-probsoln/test-probsoln.tex --output tests/output/test-probsoln --html

test-lists : app tests/output
	rm -f -r tests/output/test-lists
	bin/texparsertest --in tests/test-lists/test-lists.tex --output tests/output/test-lists --html

test-datatool : app tests/output
	rm -f -r tests/output/test-datatool
	bin/texparsertest --debug --in tests/test-datatool/test-datatool.tex --output tests/output/test-datatool --html

test-datatool-v3 : app tests/output
	rm -f -r tests/output/test-datatool-v3
	bin/texparsertest --debug-mode process-generic-cs,expansion,process,process-stack,io --log tests/output/test-datatool-v3.log --in tests/test-datatool-v3/test-datatool-v3.tex --output tests/output/test-datatool-v3 --html

test-jmlr	: app tests/output
	rm -f -r tests/output/test-jmlr
	bin/texparsertest --in tests/test-jmlr/test-jmlr.tex --output tests/output/test-jmlr

test-mkimages : app tests/output
	rm -f -r tests/output/test-mkimages
	bin/texparsertest --debug --in tests/test-mkimages/test-mkimages.tex --output tests/output/test-mkimages --html

app     : lib lib/texparserlib.jar lib/texparsertest.jar \
	  lib/resources \
	  bin/texparsertest

lib/texparserlib.jar      : lib classes/com/dickimawbooks/texparserlib/TeXParser.class 
	cd classes; \
	jar cf ../lib/texparserlib.jar \
	com/dickimawbooks/texparserlib/*.class \
	com/dickimawbooks/texparserlib/*/*.class \
	com/dickimawbooks/texparserlib/*/*/*.class 

lib/texparsertest.jar      : java/test/Manifest.txt \
			classes/com/dickimawbooks/texparsertest/TeXParserApp.class 
	cd classes; \
	jar cmf ../java/test/Manifest.txt ../lib/texparsertest.jar \
	com/dickimawbooks/texparsertest/*.class \
	com/dickimawbooks/texparsertest/*/*.class 

classes/com/dickimawbooks/texparsertest/TeXParserApp.class   : classes/com/dickimawbooks/texparsertest \
	java/test/*.java java/test/*/*.java \
	lib/texparserlib.jar
	cd java/test; \
	javac -d ../../classes \
	 -Xlint:unchecked -Xlint:deprecation \
	-cp ../../lib/texparserlib.jar \
	*.java */*.java

classes/com/dickimawbooks/texparserlib/TeXParser.class   : classes/com/dickimawbooks/texparserlib \
	java/lib/*.java java/lib/*/*.java java/lib/*/*/*.java
	cd java/lib; \
	javac $(target_version) -d ../../classes -Xlint:unchecked -Xlint:deprecation *.java */*.java */*/*.java

classes/com/dickimawbooks/texparsertest    :
	mkdir -p classes/com/dickimawbooks/texparsertest

classes/com/dickimawbooks/texparserlib    :
	mkdir -p classes/com/dickimawbooks/texparserlib

resources/LICENSE       :
	cd resources; ln -s ../doc/LICENSE

lib/resources   :
	cd lib; ln -s ../resources

lib     :
	mkdir -p lib

lib/texparser-splash.png  :
	cd lib; ln -s ../images/texparser-splash.png

source	:
	cd ..; git archive -o texparser-src.zip HEAD; \
	mkdir -p texparser-src; \
	unzip -d texparser-src texparser-src.zip; \
	rm texparser-src.zip; \
	find texparser-src -name .gitignore -exec rm {} \; ;\
	zip -r texparser-src.zip texparser-src; \
	rm -r texparser-src; mv texparser-src.zip dist


clean   :
	\rm -f classes/com/dickimawbooks/texparserlib/*.class
	\rm -f classes/com/dickimawbooks/texparsertest/*.class

squeaky :
	\rm -f lib/texparsertest.jar
	\rm -f lib/texparserlib.jar

