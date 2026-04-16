//////////////////////////////////TEST libs.BdpLib.Transform.splitPairKeyConcatenation//////////////////////////////////
/**
 * Run testSplitPairKeyConcatenation() method from groovy console to test all the units, fell free to add more.
 */
def splitPairKeyConcatenationTestUnit1() {
	final def transform = libs.BdpLib.Transform
	def separator = "\\+"
	def result = transform.splitPairKeyConcatenation(null, separator)
	assert result == null : "Null pairKey should return null!"
}

def splitPairKeyConcatenationTestUnit2() {
	final def transform = libs.BdpLib.Transform
	def separator = "\\+"
	def pairKey = "uniqueKeyWithoutSeparator"
	def expectedResult = [first: pairKey]
	def result = transform.splitPairKeyConcatenation(pairKey, separator)
	assert result == expectedResult : "First key was expected!"
}

def splitPairKeyConcatenationTestUnit3() {
	final def transform = libs.BdpLib.Transform
	def separator = "\\+"
	def expectedResult = [first: "firstKeyOnly", last: ""]
	def result = transform.splitPairKeyConcatenation("firstKeyOnly+", separator)
	assert result == expectedResult : "Pair key expected with last empty!"
}

def splitPairKeyConcatenationTestUnit4() {
	final def transform = libs.BdpLib.Transform
	def separator = "\\+"
	def expectedResult = [first: "", last: "lastKeyOnly"]
	def result = transform.splitPairKeyConcatenation("+lastKeyOnly", separator)
	assert result == expectedResult : "Pair key expected with first empty!"
}

def splitPairKeyConcatenationTestUnit5() {
	final def transform = libs.BdpLib.Transform
	def separator = "\\+"
	def expectedResult = [first: "first", last: "last"]
	def result = transform.splitPairKeyConcatenation("first+last", separator)
	assert result == expectedResult : "Complete pair expected!"
}

def splitPairKeyConcatenationTestUnit6() {
	final def transform = libs.BdpLib.Transform
	def separator = "\\+"
	def exceptionThrown = false

	try {
		transform.splitPairKeyConcatenation("first+last+extra", separator)
	} catch (e) {
		exceptionThrown = true
	}
	assert exceptionThrown : "Expected SomeException to be thrown, but it wasn't."
}

def testSplitPairKeyConcatenation() {
	splitPairKeyConcatenationTestUnit1();
	splitPairKeyConcatenationTestUnit2();
	splitPairKeyConcatenationTestUnit3();
	splitPairKeyConcatenationTestUnit4();
	splitPairKeyConcatenationTestUnit5();
	splitPairKeyConcatenationTestUnit6();
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
