#need to count words (collecting data), filter that data, then comb over the whole data set again to collect all comments with those words

DATADIR="/data_reitter/jrc-sociolinguistics"

DATAFOLDER="$DATADIR/modred/"
WORDDATA="$DATADIR/wordmap/"
FILTERDATA="$DATADIR/filtermap/"
KEYWORDCOMMENTS="$DATADIR/organized-comments-fix/"

CLASSPATH=.:"/home/jrc436/sociolinguistics/Sociolinguistics/lib/*"
MEM="-Xmx200g"

#1 is name of the process, 2 is *all* args to that process
function runJava {
	java -cp $CLASSPATH $MEM $1 $2 2> "${1##*.}-error.log" > "${1##*.}-out.log"
}




#runJava "worddata.WordDataMain" "$DATAFOLDER $WORDDATA reddit util.wordmap.EarlyDateCombine util.wordmap.CountCombine END"
#runJava "filter.FilterMain" "$WORDDATA $FILTERDATA filter.DateFilter Lexical Frequency END"
runJava "keywords.OrganizeFromFilterMain" "$DATAFOLDER $KEYWORDCOMMENTS reddit $FILTERDATA END"
