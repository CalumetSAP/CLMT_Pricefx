import groovy.transform.Field
import net.pricefx.server.dto.calculation.ResultMatrix

// @formatter:off
/**
 * The Stopwatch library can be used to measure performance of code blocks within a single formula element or across
 * multiple elements. It relies on System.nanoTime() for measuring the duration of the time blocks. Its main advantage
 * in contrary to the standard trace in the Debug mode is that it can measure whole run time of multiple code blocks
 * within a single formula element and not only specific Pricefx API calls and elements' total run time.
 * <p>
 * To measure performance of your code using Stopwatch, you can use two methods. Either enclose your code with
 * {@link #start(String)}, {@link #lap(String)} and {@link #stop()} calls or use the {@link #measure(String, Closure)} function
 * with a closure whose performance will be measured.
 * <p>
 * The Stopwatch supports measuring of nested code blocks, e.g. performance of code within a for-loop.
 * <p>
 * Example for {@code start()}, {@code lap()} and {@code stop()}: *
 * <blockquote>
 *   <pre>
 *    def Stopwatch = libs.StopwatchLib.Stopwatch // make a shortcut to the library
 *
 *    Stopwatch.start "Element A"
 *    Stopwatch.start "Block 1"  // creates a nested time block within Element A
 *    Stopwatch.start "Block 1.1"  // creates a nested time block within Block 1
 *    // ... your measured code
 *    Stopwatch.lap "Block 1.2"  // ends measuring of Block 1.1 and starts Block 1.2
 *    // ... your measured code
 *    Stopwatch.stop   // ends measuring of Block 1.2
 *    Stopwatch.start "Block 2"  // creates a nested time block within Element A
 *    // ... your measured code
 *    Stopwatch.stop   // ends measuring of Block 2
 *    Stopwatch.stop   // ends measuring of Element A
 *
 *    Stopwatch.trace
 *    </pre>
 *  </blockquote>
 *
 * Example for measure():
 *  <blockquote>
 *    <pre>
 *    def Stopwatch = libs.StopwatchLib.Stopwatch // make a shortcut to the library
 *
 *    Stopwatch.measure "Element A", {
 *      Stopwatch.measure "Block 1", {
 *        Stopwatch.measure "Block 1.1", {
 *          // ... your measured code
 *        }
 *        Stopwatch.measure "Block 1.2", {
 *          // ... your measured code
 *        }
 *      }
 *      Stopwatch.measure "Block 2", {
 *        // ... your measured code
 *      }
 *    }
 *
 *    Stopwatch.trace
 *    </pre>
 *  </blockquote>
 *
 * The library supports four kinds of reports: via api.trace, via ResultMatrix, println or custom report. All you need to do
 * is to call one of the following methods after the last stop() or measure() method.
 *
 * <ul>
 *  <li>{@link #trace()}              prints the results using api.trace (three columns will be printed)</li>
 *  <li>{@link #toResultMatrix()}     inserts the results into a ResultMatrix (with three columns) and returns it</li>
 *  <li>{@link #print()}              prints the results to the system console using println for each line</li>
 *  <li>{@link #toList()}             returns the results in a List which contains a List with three elements (columns)</li>
 * </ul>
 *
 * An example result looks like this:
 * <blockquote>
 * <pre>
 * STOPWATCH REPORT | % to Overall              | % to Parent
 * Element A        | ▓▓▓▓▓▓▓▓▓▓ 610 ms (100 %) | ▓▓▓▓▓▓▓▓▓▓ 610 ms (100 %)
 * ├─ Block 1       | ▓▓▓▓▓░░░░░ 305 ms (50 %)  | ▓▓▓▓▓░░░░░ 305 ms (50 %)
 * │  ├─ Block 1.1  | ▓▓▓░░░░░░░ 200 ms (33 %)  | ▓▓▓▓▓▓▓░░░ 200 ms (66 %)
 * │  └─ Block 1.2  | ▓▓░░░░░░░░ 101 ms (17 %)  | ▓▓▓░░░░░░░ 101 ms (33 %)
 * └─ Block 2       | ▓▓▓▓▓░░░░░ 299 ms (49 %)  | ▓▓▓▓▓░░░░░ 299 ms (49 %)
 * </pre>
 * </blockquote>
 * The first column contains the name of the measured time block with depicted hierarchy of nesting. The second column
 * displays each time block's duration ratio to the root (1st level) time block. The third column displays each time
 * block's duration ratio to its immediate parent time block.
 */
// @formatter:on
void javadoc() {
  // an empty method just to bind the "class" Javadoc to a method
}

// the column headers used in the reports
@Field final List HEADER = ["STOPWATCH REPORT", "% to Overall", "% to Parent"]

// characters used in the reports
@Field final String CHAR_SIBLING = "├─"
@Field final String CHAR_LAST = "└─"
@Field final String CHAR_HAS_CHILD = "│ "
@Field final String CHAR_FILLED = "▓"
@Field final String CHAR_EMPTY = "░"
@Field final String SEPARATOR = "::"

/**
 * Calls the method {@link #start(String)} while converting the given Object to a String.
 * It expects the given Object to be a formula element object, thus you should call it in the following way:
 *
 * <blockquote>
 * <pre>
 *  start(this)
 * </pre>
 * </blockquote>
 *
 * @param elementName the formula element Object
 */
void start(Object elementName) {
  start(_elementName(elementName))
}

/**
 * Starts measuring a time block with the given name. The Stopwatch will measure the time duration until the method
 * {@link #stop()} is called.
 * <p>
 * You can nest time blocks by calling {@link #start(String)} one after each other. Each time block must end with
 * a corresponding call to {@link #stop()}.
 * <p>
 * Instead of calling
 * <pre>start("A"), stop(), start("B"), stop()</pre>
 * you can also call
 * <pre>start("A"), lap("B"), stop()</pre>
 *
 * Example:
 * <blockquote>
 *   <pre>
 *    def Stopwatch = libs.StopwatchLib.Stopwatch // make a shortcut to the library
 *
 *    Stopwatch.start "Element A"
 *    Stopwatch.start "Block 1"  // creates a nested time block within Element A
 *    Stopwatch.start "Block 1.1"  // creates a nested time block within Block 1
 *    // ... your measured code
 *    Stopwatch.lap "Block 1.2"  // ends measuring of Block 1.1 and starts Block 1.2
 *    // ... your measured code
 *    Stopwatch.stop   // ends measuring of Block 1.2
 *    Stopwatch.start "Block 2"  // creates a nested time block within Element A
 *    // ... your measured code
 *    Stopwatch.stop   // ends measuring of Block 2
 *    Stopwatch.stop   // ends measuring of Element A
 *
 *    Stopwatch.trace
 *    </pre>
 *  </blockquote>
 * You can also use a more readable syntax with method {@link #measure(String, Closure)} instead
 *
 * @param blockName the name of the time block to measure
 *
 * @see #stop()
 * @see #lap(String)
 * @see #measure(String, Closure)
 */
void start(String blockName) {
  def st = _stopwatch()

  // if there is currently no path, initialize it
  if (!st.path) {
    st.path = blockName
  }

  def currentBlock = st.blocks[st.path]
  def block = null

  // if the current block is already running, create a new sub-block
  if (currentBlock.status == 1 && blockName != currentBlock.name) {
    int parentLevel = st.blocks[st.path].level
    st.path += SEPARATOR + blockName
    block = st.blocks[st.path]
    block.level = parentLevel + 1
    if (block.status == 1) {
      api.throwException("Cannot start() a block which is already running")
    }
  }
  // otherwise start/resume the current block
  else {
    block = st.blocks[st.path]
  }

  // start the block
  st.running += 1
  block.path = st.path
  block.name = blockName
  block.stop = 0L
  block.status = 1
  block.start = System.nanoTime()
}

/**
 * Stops measuring the time of the time block started with {@link #start(String)}. This method should always
 * come in pair with a prior corresponding call to the {@link #start(String)} method, or after a {@link #lap(String)} method
 * is called.
 *
 * @see #start(String)
 * @see #lap(String)
 */
void stop() {
  def stopTime = System.nanoTime()

  def st = _stopwatch()
  if (!st.path) {
    api.throwException("Cannot stop() when nothing has started yet")
  }

  // check if we can stop the block
  def block = st.blocks[st.path]
  if (block.status != 1) {
    api.throwException("Cannot stop() a block which has not started: " + st.path)
  }

  // remove the last block form the path
  if (st.path.contains(SEPARATOR)) {
    st.path = st.path[0..(st.path.lastIndexOf(SEPARATOR) - 1)]
  } else {
    st.path = null
  }

  // stop the block
  block.stop = stopTime
  block.diff += (block.stop - block.start)
  block.status = 0
  block
}

// @formatter:off
/**
 * Starts measuring a time block with the given name. The Stopwatch will measure the time duration of the given closure.
 * You can nest the calls to the {@link #measure(String, Closure)} method.
 * <p>
 * An alternative is to enclose your code with {@link #start(String)} and {@link #stop()} methods.
 * <p>
 * Example:
 *  <blockquote>
 *    <pre>
 *    def Stopwatch = libs.StopwatchLib.Stopwatch // make a shortcut to the library
 *
 *    Stopwatch.measure "Element A", {
 *      Stopwatch.measure "Block 1", {
 *        Stopwatch.measure "Block 1.1", {
 *          // ... your measured code
 *        }
 *        Stopwatch.measure "Block 1.2", {
 *          // ... your measured code
 *        }
 *      }
 *      Stopwatch.measure "Block 2", {
 *        // ... your measured code
 *      }
 *    }
 *
 *    Stopwatch.trace
 *    </pre>
 *  </blockquote>
 *
 * @param blockName the name of the time block to measure
 * @param closure the closure to measure
 *
 * @see #start(String)
 * @see #stop()
 */
// @formatter:on
void measure(String blockName, Closure closure) {
  start(blockName)
  closure()
  stop()
}

/**
 * Works same way as measure(String blockName, Closure closure) with one difference. It returns the result of closure.
 * Its handy when you want to analyze multiple elements with multiple exit points and those multiple elements has dependency on each other.
 *
 * @param blockName the name of the time block to measure
 * @param closure the closure to measure
 *
 * @return the closure invocation.
 *
 * @see #measure(String blockName, Closure closure)
 */
def measureWithResult(String blockName, Closure closure) {
  start(blockName)
  def result = closure()
  stop()
  return result
}

/**
 * Stops measuring the previous time block and starts a new one. Essentially it just calls stop() and start(blockName).
 * <p>
 * For an example see the {@link #start(String)} method.
 *
 * @param blockName the name of the time block to measure
 */
void lap(String blockName) {
  stop()
  start(blockName)
}

/**
 * Groovy getter to enable the Stopwatch.stop syntax (without parentheses). Calls the {@link #stop()} method.
 */
def getStop() {
  stop()
}

/**
 * Creates a time report and prints it using {@code api.trace}. If you provide the {@code blockName} parameter,
 * the report will be created only for the specified time block and its children.
 * The result is visually the same as calling the {@link #toString(boolean, String)} method.
 *
 * @param verbose if true, the branches will also contain information of each block's duration ratio to each of its
 * hierarchical parent.
 * @param blockName optionally provide the name of the block to report, otherwise the report will be created for all
 *
 * @see #toString(boolean, String)
 */
void trace(boolean verbose = false, String blockName = null) {
  api.trace(HEADER[0], HEADER[1], HEADER[2])
  for (line in toList(verbose, blockName)) {
    api.trace(line[0], line[1], line[2])
  }
}

/**
 * Creates a time report and puts it into a {@code ResultMatrix}. If you provide the {@code blockName} parameter,
 * the report will be created only for the specified time block and its children.
 * The result is visually the same as calling the {@link #toString(boolean, String)} method.
 *
 * @param verbose if true, the branches will also contain information of each block's duration ratio to each of its
 * hierarchical parent.
 * @param blockName optionally provide the name of the block to report, otherwise the report will be created for all
 *
 * @return the ResultMatrix with the report
 *
 * @see #toString(boolean, String)
 */
ResultMatrix toResultMatrix(boolean verbose = false, String blockName = null) {
  def m = api.newMatrix(HEADER[0], HEADER[1], HEADER[2])
  for (line in toList(verbose, blockName)) {
    m.addRow([(HEADER[0]): line[0], (HEADER[1]): line[1], (HEADER[2]): line[2]])
  }
  return m
}

/**
 * Creates a time report and returns it in a formatted {@code String}. If you provide the {@code blockName} parameter,
 * the report will be created only for the specified time block and its children.
 * The result is visually the same as calling the {@link #toString(boolean, String)} method.
 * <p>
 * The result looks like this:
 * <blockquote>
 * <pre>
 *  STOPWATCH REPORT | % to Overall              | % to Parent
 *  Element A        | ▓▓▓▓▓▓▓▓▓▓ 610 ms (100 %) | ▓▓▓▓▓▓▓▓▓▓ 610 ms (100 %)
 *  ├─ Block 1       | ▓▓▓▓▓░░░░░ 305 ms (50 %)  | ▓▓▓▓▓░░░░░ 305 ms (50 %)
 *  │  ├─ Block 1.1  | ▓▓▓░░░░░░░ 200 ms (33 %)  | ▓▓▓▓▓▓▓░░░ 200 ms (66 %)
 *  │  └─ Block 1.2  | ▓▓░░░░░░░░ 101 ms (17 %)  | ▓▓▓░░░░░░░ 101 ms (33 %)
 *  └─ Block 2       | ▓▓▓▓▓░░░░░ 299 ms (49 %)  | ▓▓▓▓▓░░░░░ 299 ms (49 %)
 * </pre>
 * </blockquote>
 * The first column contains the name of the measured time block with depicted hierarchy of nesting. The second column
 * displays each time block's duration ratio to the root (1st level) time block. The third column displays each time
 * block's duration ratio to its immediate parent time block.
 * <p>
 * If {@code verbose} is true, each branch will also show an information about the child block's duration ratio to its
 * parent, like this:
 * <blockquote>
 * <pre>
 *  STOPWATCH REPORT            | % to Overall              | % to Parent
 *  Element A                   | ▓▓▓▓▓▓▓▓▓▓ 610 ms (100 %) | ▓▓▓▓▓▓▓▓▓▓ 610 ms (100 %)
 *  ├─(50 %) Block 1            | ▓▓▓▓▓░░░░░ 305 ms (50 %)  | ▓▓▓▓▓░░░░░ 305 ms (50 %)
 *  │ (33 %) ├─(50 %) Block 1.1 | ▓▓▓░░░░░░░ 200 ms (33 %)  | ▓▓▓▓▓▓▓░░░ 200 ms (66 %)
 *  │ (33 %) └─(17 %) Block 1.2 | ▓▓░░░░░░░░ 101 ms (17 %)  | ▓▓▓░░░░░░░ 101 ms (33 %)
 *  └─(49 %) Block 2            | ▓▓▓▓▓░░░░░ 299 ms (49 %)  | ▓▓▓▓▓░░░░░ 299 ms (49 %)
 * </pre>
 * </blockquote>
 *
 * @param verbose if true, the branches will also contain information of each block's duration ratio to each of its
 * hierarchical parent.
 * @param blockName optionally provide the name of the block to report, otherwise the report will be created for all
 *
 * @return the report as a formatted String
 *
 */
String toString(boolean verbose = false, String blockName = null) {
  List rows = toList(verbose, blockName)

  // determine the column widths
  // (for each of 3 columns, get the max size of String in the column + the header size) and take the longest
  def widths = (0..2).collect { col -> (rows.collect { row -> row[col].size() } + HEADER[col].size()).max() }

  // print the header
  // (for each of 3 columns, print the header padded to the previously calculated column width)
  String out = ((0..2).collect { col -> HEADER[col].padRight(widths[col], " ") }.join(" | ")).concat("\n")

  // print the rows
  // (for each row, for each of 3 columns, pad the String to the calculated colum width, join columns with "|" and
  // rows with "\n")
  return out.concat(
      rows.collect { row ->
        (0..2).collect { col ->
          row[col].padRight(widths[col], " ")
        }.join(" | ")
      }.join("\n"))
}

/**
 * Creates a time report and returns it in a List of Lists. The inner list has always three elements (columns).
 * If you provide the {@code blockName} parameter, the report will be created only for the specified time block
 * and its children.
 * <p>
 * The result looks like this:
 * <blockquote>
 * <pre>
 * [
 *  [ "STOPWATCH REPORT", "% to Overall", "% to Parent],
 *  [ "Element A",        "▓▓▓▓▓▓▓▓▓▓ 610 ms (100 %)",  "▓▓▓▓▓▓▓▓▓▓ 610 ms (100 %)" ]
 *  [ "├─ Block 1",       "▓▓▓▓▓░░░░░ 305 ms (50 %)",   "▓▓▓▓▓░░░░░ 305 ms (50 %)" ]
 *  [ "│  ├─ Block 1.1",  "▓▓▓░░░░░░░ 200 ms (33 %)",   "▓▓▓▓▓▓▓░░░ 200 ms (66 %) ]
 *  [ "│  └─ Block 1.2",  "▓▓░░░░░░░░ 101 ms (17 %)",   "▓▓▓░░░░░░░ 101 ms (33 %) ]
 *  [ "└─ Block 2",       "▓▓▓▓▓░░░░░ 299 ms (49 %)",   "▓▓▓▓▓░░░░░ 299 ms (49 %) ]
 * ]
 * </pre>
 * </blockquote>
 * The first column contains the name of the measured time block with depicted hierarchy of nesting. The second column
 * displays each time block's duration ratio to the root (1st level) time block. The third column displays each time
 * block's duration ratio to its immediate parent time block.
 * <p>
 * If {@code verbose} is true, each branch will also show an information about the child block's duration ratio to its
 * parent. See {@link #toString(boolean)} for how this looks like.
 *
 * @param verbose if true, the branches will also contain information of each block's duration ratio to each of its
 * hierarchical parent.
 * @param blockName optionally provide the name of the block to report, otherwise the report will be created for all
 *
 * @return the report in a List of Lists
 */
List toList(boolean verbose = false, String blockName = null) {

  def out = []

  def st = _stopwatch()

  if (st.path) {
    def running = st.blocks.collect { it.value }.reverse().find { e -> e.start > 0 && e.stop == 0 }
    api.throwException("You have stopwatch running on time block '$running.name'. You need to call stop()")
  }

  def parentStack = [] as Stack
  def previousBlock = null

  // if the blockName is specified, include only blocks with this name and its children (determined by the block's path (e.key))
  def blocks = (blockName ? st.blocks.findAll { e -> e.key.contains(blockName + SEPARATOR) || e.value.name == blockName } : st.blocks).values() as ArrayList

  // for each block
  for (int bIndex = 0; bIndex < blocks.size(); ++bIndex) {

    def block = blocks[bIndex]

    // determine the parent by pushing the previous block on the parentStack if its level is lower,
    // or popping all blocks whose level is higher
    if (previousBlock) {
      if (previousBlock.level < block.level) {
        parentStack.push(previousBlock)
      } else if (previousBlock.level > block.level) {
        while (previousBlock?.level > block.level) {
          previousBlock = parentStack.pop()
        }
      }
    }

    def diff = block.diff
    def toParentRatio = toRootRatio = 1.0
    def prefix = ""

    // get all blocks left after this block
    def nextBlocks = (bIndex < blocks.size() - 1) ? blocks[(bIndex + 1)..-1] : []

    // for each parent of this block
    parentStack.eachWithIndex { parentBlock, i ->

      // determine if this block is an immediate child of the parent block
      def isImmediateParent = (parentBlock.level == block.level - 1)

      // if this parent is the root (index == 0), calculate the % to Root ratio
      if (i == 0 && parentBlock.diff) {
        toRootRatio = diff / parentBlock.diff
      }

      // if this parent is the immediate parent of the block, calculate the % to Parent ratio
      if (isImmediateParent && parentBlock.diff) {
        toParentRatio = diff / parentBlock.diff
      }

      // children = take from the nextBlocks until the level is higher than the parent's level
      def children = nextBlocks.takeWhile { it.level > parentBlock.level }

      // determine if the parent block has another immediate child
      def parentHasAnotherImmediateChild = children.any { it.level == parentBlock.level + 1 }

      // determine the rendered unicode "box" symbol which forms the tree structure
      def sign = "  "
      if (isImmediateParent) {
        if (parentHasAnotherImmediateChild) {
          sign = CHAR_SIBLING
        } else {
          sign = CHAR_LAST
        }
      } else if (parentHasAnotherImmediateChild) {
        sign = CHAR_HAS_CHILD
      }
      prefix += sign + (verbose ? "(" + _formatPct(diff / parentBlock.diff, true) + ") " : " ")
    }

    out << [prefix + block.name, _renderPct(diff, toRootRatio), _renderPct(diff, toParentRatio)]

    previousBlock = block
  }

  return out
}

/**
 * !!! Private method, DO NOT USE !!!
 *
 * @return the Stopwatch object which stores the internal configuration in api.global.__stopwatch
 */
Map _stopwatch() {
  if (!api.global.__stopwatch) {
    api.global.__stopwatch = [
        // the current path
        path   : null,
        running: 0,
        // the measured time blocks
        blocks : [:].withDefault {
          [
              name  : null,
              path  : null,
              level : 0,
              status: 0L, // 0 stopped/paused, 1 running
              start : 0L,
              stop  : 0L,
              diff  : 0L
          ]
        }]
  }
  return (Map) api.global.__stopwatch
}

/**
 * !!! Private method, DO NOT USE !!!
 */
def _elementName(obj) {
  def element = obj.toString()
  element.substring(element.indexOf("_") + 1, element.lastIndexOf("_"))
}

/**
 * !!! Private method, DO NOT USE !!!
 */
def _formatMs(time) {
  (time / 1_000_000).setScale(0, BigDecimal.ROUND_HALF_UP) + " ms"
}

/**
 * !!! Private method, DO NOT USE !!!
 */
def _formatPct(number, boolean pad = false) {
  def n = (number * 100).setScale(0, BigDecimal.ROUND_HALF_UP)
  if (pad) {
    return n.toString().padLeft(2, "0") + " %"
  }
  return n + " %"
}

/**
 * !!! Private method, DO NOT USE !!!
 */
def _renderPct(time, ratio) {
  List out = []
  def x = (ratio * 10).setScale(0, BigDecimal.ROUND_HALF_UP).toInteger()
  x.times { out << CHAR_FILLED }
  (10 - x).times { out << CHAR_EMPTY }
  return out.join() + " " + _formatMs(time) + " (" + _formatPct(ratio) + ")"
}
