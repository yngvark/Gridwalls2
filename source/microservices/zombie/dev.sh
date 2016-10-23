if [[ $1 == "-b" ]]; then
	echo --- Building ---
	rm -rf container/app
	cd source
	gradle build installDist
	cd ..
	cp -r source/build/install/zombie container/app
	exit;
fi

if [[ $1 == "-t" ]]; then
	echo --- Running with in_pipe and out_pipe ---
	./container/app/bin/zombie < container/app/in_pipe | tee container/app/out_pipe
	exit;
fi

echo --- Running normally ---
./container/app/bin/zombie 

