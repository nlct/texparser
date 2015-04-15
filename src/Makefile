APP_VERSION:=$(shell grep "public static final String appVersion" java/app/TeXParserApp.java | sed "s/public\sstatic\sfinal\sString\sappVersion=//" | tr -d "\"\; ")
IZPACKDIR=/usr/local/IzPack

test	: test-obs test-sw test-abs test-article test-nested test-doc \
	  test-probsoln

tests/output	:
	mkdir -p tests/output

test-nested	: app tests/output
	rm -f -r tests/output/test-nested-env-group
	bin/texparserapp --in tests/test-nested-env-group/test-nested-env-group.tex --output tests/output/test-nested-env-group

test-obs	: app tests/output
	rm -f -r tests/output/test-obsolete
	bin/texparserapp --in tests/test-obsolete/test-obs.tex --output tests/output/test-obsolete

test-sw	: app tests/output
	rm -f -r tests/output/test-sw
	bin/texparserapp --in tests/test-sw/test-sw.tex --output tests/output/test-sw

test-abs : app tests/output
	rm -f -r tests/output/test-abs
	bin/texparserapp --in tests/test-abs/test-abs.tex --output tests/output/test-abs --html

test-article : app tests/output
	rm -f -r tests/output/test-article
	bin/texparserapp --in tests/test-article/test-article.tex --output tests/output/test-article --html

test-doc : app tests/output
	rm -f -r tests/output/test-doc
	bin/texparserapp --in tests/test-doc/test-doc.tex --output tests/output/test-doc --html

test-probsoln : app tests/output
	rm -f -r tests/output/test-probsoln
	bin/texparserapp --in tests/test-probsoln/test-probsoln.tex --output tests/output/test-probsoln --html

app     : lib lib/texparserlib.jar lib/texparserapp.jar \
	  lib/resources lib/texparser-splash.png \
	  bin/texparserapp lib/icons \
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

lib/texparserapp.jar      : java/app/Manifest.txt \
			classes/com/dickimawbooks/texparserapp/TeXParserApp.class 
	cd classes; \
	jar cmf ../java/app/Manifest.txt ../lib/texparserapp.jar \
	com/dickimawbooks/texparserapp/*.class \
	com/dickimawbooks/texparserapp/*/*.class \
	com/dickimawbooks/texparserapp/icons/*.png

classes/com/dickimawbooks/texparserapp/TeXParserApp.class   : classes/com/dickimawbooks/texparserapp \
	java/app/*.java java/app/*/*.java java/app/icons/*.png \
	lib/javahelp2.jar lib/jlfgr-1_0.jar lib/texparserlib.jar
	cd java/app; \
	javac -d ../../classes \
	 -Xlint:unchecked -Xlint:deprecation \
	-cp ../../lib/javahelp2.jar:../../lib/jlfgr-1_0.jar:../../lib/texparserlib.jar \
	*.java */*.java
	mkdir -p classes/com/dickimawbooks/texparserapp/icons
	cp java/app/icons/*.png classes/com/dickimawbooks/texparserapp/icons/

classes/com/dickimawbooks/texparserlib/TeXParser.class   : classes/com/dickimawbooks/texparserlib \
	java/lib/*.java java/lib/*/*.java java/lib/*/*/*.java
	cd java/lib; \
	javac -d ../../classes -Xlint:unchecked -Xlint:deprecation *.java */*.java */*/*.java

classes/com/dickimawbooks/texparserapp    :
	mkdir -p classes/com/dickimawbooks/texparserapp

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
	\rm -f classes/com/dickimawbooks/texparserapp/*.class

squeaky :
	\rm -f lib/texparserapp.jar
	\rm -f lib/texparserlib.jar

