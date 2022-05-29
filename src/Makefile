ifeq ($(origin JAVA8BOOTCLASSLIB),undefined)
TARGET_VERSION :=
else
TARGET_VERSION := -target 1.8 -source 1.8 -bootclasspath $(JAVA8BOOTCLASSLIB)/rt.jar:$(JAVA8BOOTCLASSLIB)/charsets.jar:$(JAVA8BOOTCLASSLIB)/jfr.jar:$(JAVA8BOOTCLASSLIB)/jfxswt.jar:$(JAVA8BOOTCLASSLIB)/jce.jar:$(JAVA8BOOTCLASSLIB)/jsse.jar:$(JAVA8BOOTCLASSLIB)/deploy.jar:$(JAVA8BOOTCLASSLIB)/plugin.jar:$(JAVA8BOOTCLASSLIB)/javaws.jar:$(JAVA8BOOTCLASSLIB)/resources.jar:$(JAVA8BOOTCLASSLIB)/management-agent.jar:$(JAVA8BOOTCLASSLIB)/security/US_export_policy.jar:$(JAVA8BOOTCLASSLIB)/security/local_policy.jar:$(JAVA8BOOTCLASSLIB)/ext/cldrdata.jar:$(JAVA8BOOTCLASSLIB)/ext/dnsns.jar:$(JAVA8BOOTCLASSLIB)/ext/jfxrt.jar:$(JAVA8BOOTCLASSLIB)/ext/nashorn.jar:$(JAVA8BOOTCLASSLIB)/ext/sunpkcs11.jar:$(JAVA8BOOTCLASSLIB)/ext/localedata.jar:$(JAVA8BOOTCLASSLIB)/ext/sunec.jar:$(JAVA8BOOTCLASSLIB)/ext/jaccess.jar:$(JAVA8BOOTCLASSLIB)/ext/zipfs.jar:$(JAVA8BOOTCLASSLIB)/ext/sunjce_provider.jar
endif

test	: test-obs test-abs test-article test-nested test-doc \
	  test-probsoln test-lists test-datatool test-mkimages \
	  test-jmlrutils test-glossaries

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

test-article : app tests/test-article/test-article.pdf tests/output
	rm -f -r tests/output/test-article
	bin/texparsertest --debug --in tests/test-article/test-article.tex --output tests/output/test-article --html

tests/test-article/test-article.pdf	: tests/test-article/test-article.tex
					cd tests/test-article; arara test-article

test-glossaries : app tests/test-glossaries/test-glossaries.pdf tests/output
	rm -f -r tests/output/test-glossaries
	bin/texparsertest --debug --in tests/test-glossaries/test-glossaries.tex --output tests/output/test-glossaries --html

tests/test-glossaries/test-glossaries.pdf	: tests/test-glossaries/test-glossaries.tex
					cd tests/test-glossaries; arara test-glossaries

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
	javac $(TARGET_VERSION) -d ../../classes -Xlint:unchecked -Xlint:deprecation *.java */*.java */*/*.java

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

