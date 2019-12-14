APP_VERSION:=$(shell grep "public static final String appVersion" java/test/TeXParserApp.java | sed "s/public\sstatic\sfinal\sString\sappVersion=//" | tr -d "\"\; ")
IZPACKDIR=/usr/local/IzPack

test	: test-obs test-abs test-article test-nested test-doc \
	  test-probsoln test-lists test-datatool test-mkimages \
	  test-jmlrutils

tests/output	:
	mkdir -p tests/output

test-nested	: app tests/output
	rm -f -r tests/output/test-nested-env-group
	bin/texparsertest --debug --in tests/test-nested-env-group/test-nested-env-group.tex --output tests/output/test-nested-env-group

test-obs	: app tests/output
	rm -f -r tests/output/test-obsolete
	bin/texparsertest --debug --in tests/test-obsolete/test-obs.tex --output tests/output/test-obsolete

test-sw	: app tests/output
	rm -f -r tests/output/test-sw
	bin/texparsertest --debug --in tests/test-sw/test-sw.tex --output tests/output/test-sw

test-abs : app tests/output
	rm -f -r tests/output/test-abs
	bin/texparsertest --debug --in tests/test-abs/test-abs.tex --output tests/output/test-abs --html

test-article : app tests/output
	rm -f -r tests/output/test-article
	bin/texparsertest --debug --in tests/test-article/test-article.tex --output tests/output/test-article --html

test-jmlrutils : app tests/output
	rm -f -r tests/output/test-jmlrutils
	bin/texparsertest --debug --in tests/test-jmlrutils/test-jmlrutils.tex --output tests/output/test-jmlrutils --html

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

test-jmlr	: app tests/output
	rm -f -r tests/output/test-jmlr
	bin/texparsertest --in tests/test-jmlr/test-jmlr.tex --output tests/output/test-jmlr

test-mkimages : app tests/output
	rm -f -r tests/output/test-mkimages
	bin/texparsertest --debug --in tests/test-mkimages/test-mkimages.tex --output tests/output/test-mkimages --html

app     : lib lib/texparserlib.jar lib/texparsertest.jar \
	  lib/resources lib/texparser-splash.png \
	  bin/texparsertest lib/icons \
	  resources/helpsets/texparser-en/Map.jhm \
	  resources/LICENSE

dist    : source ../dist/texparser-installer.jar
	cp doc/texparser.pdf ../dist

../dist/texparser-installer.jar        : app installer/install.xml
	$(IZPACKDIR)/bin/compile installer/install.xml -b . \
	-o ../dist/texparser-installer.jar -k standard

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
	com/dickimawbooks/texparsertest/*/*.class \
	com/dickimawbooks/texparsertest/icons/*.png

classes/com/dickimawbooks/texparsertest/TeXParserApp.class   : classes/com/dickimawbooks/texparsertest \
	java/test/*.java java/test/*/*.java java/test/icons/*.png \
	lib/javahelp2.jar lib/jlfgr-1_0.jar lib/texparserlib.jar
	cd java/test; \
	javac -d ../../classes \
	 -Xlint:unchecked -Xlint:deprecation \
	-cp ../../lib/javahelp2.jar:../../lib/jlfgr-1_0.jar:../../lib/texparserlib.jar \
	*.java */*.java
	mkdir -p classes/com/dickimawbooks/texparsertest/icons
	cp java/test/icons/*.png classes/com/dickimawbooks/texparsertest/icons/

classes/com/dickimawbooks/texparserlib/TeXParser.class   : classes/com/dickimawbooks/texparserlib \
	java/lib/*.java java/lib/*/*.java java/lib/*/*/*.java
	cd java/lib; \
	javac -target 1.7 -source 1.7 -d ../../classes -Xlint:unchecked -Xlint:deprecation *.java */*.java */*/*.java

classes/com/dickimawbooks/texparsertest    :
	mkdir -p classes/com/dickimawbooks/texparsertest

classes/com/dickimawbooks/texparserlib    :
	mkdir -p classes/com/dickimawbooks/texparserlib

resources/LICENSE       :
	cd resources; ln -s ../doc/LICENSE

resources/helpsets/texparser-en/Map.jhm   : doc/texparser.tex
	make -C doc helpset

lib/resources   :
	cd lib; ln -s ../resources

lib/icons       :
	cd lib; ln -s ../images/icons

lib/jlfgr-1_0.jar       : 
	cd lib; ln -f -s /usr/share/java/jlfgr-1_0.jar

lib/javahelp2.jar      : 
	cd lib; ln -f -s /usr/share/java/javahelp2.jar 

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

