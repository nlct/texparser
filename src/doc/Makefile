APP_VERSION:=$(shell grep "public static final String APP_VERSION" ../java/test/TeXParserApp.java | sed "s/public\sstatic\sfinal\sString\sappVersion=//" | tr -d "\"\; ")

doc	: texparser.pdf

helpset	: texparser.pdf

texparser.pdf	: texparser.tex version.tex
		arara texparser

version.tex	: 
	echo "\\version{$(APP_VERSION)}" > version.tex

clean	:
	\rm -f texparser.{aux,log,out,toc,lof,lot,gls,glg,glo,acn,acr,alg,ist}

squeaky	:
	\rm -f texparser.pdf
