MOSSC_JAR=mossc/out/artifacts/moss_jar/moss.jar
MOSSVM=mossvm/bin/mossvm
MOSS=moss

all:
#	if [ ! -f ${MOSSC_JAR} ]; then
#		$(error you have to build the mossc.jar first - but maybe it's already in the dist folder)
#	fi
	rm -rf dist
	mkdir dist
	cp ${MOSSC_JAR} dist
	cd mossvm && $(MAKE)
	cd ..
	cp ${MOSSVM} dist
	cp ${MOSS} dist

test:
	./test.sh