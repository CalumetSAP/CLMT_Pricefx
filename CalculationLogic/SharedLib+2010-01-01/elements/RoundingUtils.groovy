import groovy.transform.Field

import java.math.RoundingMode

@Field ROUNDING_TYPES = [
        SECOND_DECIMAL: "SECOND_DECIMAL_PRECISION",
        DECIMALS      : "DECIMALS",
        WHOLE         : "WHOLE",
        TWO_DECIMALS  : "TWO_DECIMALS",
        NO_DECIMALS   : "NO_DECIMALS",
        NONE          : "NONE",
]

@Field ROUNDING_MODES = [
        UP       : "UP",
        DOWN     : "DOWN",
        HALF_UP  : "HALF_UP",
        HALF_DOWN: "HALF_DOWN",
        UNDEFINED: -1,
]

@Field ROUNDING_RULES = [
        ToX5Cents : [roundToNumber: 0.05, type: ROUNDING_TYPES.SECOND_DECIMAL],
        ToX9Cents : [roundToNumber: 0.09, type: ROUNDING_TYPES.SECOND_DECIMAL],
        To49Cents : [roundToNumber: 0.49, type: ROUNDING_TYPES.DECIMALS],
        To50Cents : [roundToNumber: 0.50, type: ROUNDING_TYPES.DECIMALS],
        To95Cents : [roundToNumber: 0.95, type: ROUNDING_TYPES.DECIMALS],
        To99Cents : [roundToNumber: 0.99, type: ROUNDING_TYPES.DECIMALS],
        ToWhole   : [roundToNumber: 0.0, type: ROUNDING_TYPES.NO_DECIMALS],
        To5Whole  : [roundToNumber: 5, type: ROUNDING_TYPES.WHOLE],
        To49Whole : [roundToNumber: 49, type: ROUNDING_TYPES.WHOLE],
        To99Whole : [roundToNumber: 99, type: ROUNDING_TYPES.WHOLE],
        RawPrice  : [roundToNumber: 0.0, type: ROUNDING_TYPES.TWO_DECIMALS],
        NoRounding: [roundToNumber: 0.0, type: ROUNDING_TYPES.NONE],
]

@Field int DEFAULT_DECIMAL_DIGITS = 2
@Field BigDecimal ONE_TENTH = 0.1

/**
 * A general function that rounds a number to N decimals
 * @param number
 * @param decimalPlaces
 * @return null if the price is null
 */
BigDecimal round(BigDecimal number, int decimalPlaces) {
    if (number == null) {
        return null
    }
    return number.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP)
}


/**
 * Rounds a price to N decimals
 * @param price
 * @param decimalPlaces
 * @return
 */
BigDecimal roundPrice(BigDecimal price, int decimalPlaces) {
    return round(price, decimalPlaces)
}


/**
 * Round a percentage to 2 decimals or upon based on a rounding lookup table name if provided.
 * @param price
 * @return null if the price is null
 */
BigDecimal roundPercent(BigDecimal price) {
    return round(price, 4)
}

/**
 * A general function that rounds up a number to N decimals
 * @param number
 * @param decimalPlaces
 * @return null if the price is null
 */
BigDecimal roundUp(BigDecimal number, int decimalPlaces) {
    if (number == null) {
        return null
    }
    return number.setScale(decimalPlaces, BigDecimal.ROUND_UP)
}


/**
 * Transforms a number to have a specific suffix.
 * @param numberToRound BigDecimal the number to be rounded. Only positive numbers are supported.
 * @param rule String name of the rule to be applied
 * supported rule: ToX5Cents, ToX9Cents, To49Cents, To50Cents, To95Cents, To99Cents, ToWhole, To5Whole, To49Whole, To99Whole, RawPrice, NoRounding
 * @param direction String the direction / mode of rounding
 * supported direction: UP, DOWN, HALF_UP, HALF_DOWN
 * @return rounded number. If numberToRound, rule, or direction is invalid, returns the raw number
 */
BigDecimal round(BigDecimal numberToRound, String rule, String direction) {
    if (!numberToRound || numberToRound < 0) {
        return numberToRound
    }

    BigDecimal result = 0.0
    def ruleConfig = ROUNDING_RULES[rule]
    def roundToNumber = ruleConfig.roundToNumber
    def roundingType = ruleConfig.type

    switch (roundingType) {
        case ROUNDING_TYPES.SECOND_DECIMAL:
            result = getRoundedNumber(getRoundedDownToXxDecimal(numberToRound, roundToNumber), getRoundedUpToXxDecimal(numberToRound, roundToNumber), numberToRound, direction)
            break
        case ROUNDING_TYPES.DECIMALS:
            if (roundToNumber < 1) {
                result = getRoundedNumber(getRoundedDownDecimal(numberToRound, roundToNumber), getRoundedUpDecimal(numberToRound, roundToNumber), numberToRound, direction)
            } else {
                result = numberToRound
            }
            break
        case ROUNDING_TYPES.WHOLE:
            if (roundToNumber >= 1) {
                result = getRoundedNumber(getRoundedDownWhole(numberToRound, roundToNumber), getRoundedUpWhole(numberToRound, roundToNumber), numberToRound, direction)
            } else {
                result = numberToRound
            }
            break
        case ROUNDING_TYPES.TWO_DECIMALS:
            int mode = getRoundingModeEnum(direction)
            result = mode != ROUNDING_MODES.UNDEFINED ? numberToRound.setScale(2, mode) : numberToRound
            break
        case ROUNDING_TYPES.NO_DECIMALS:
            int mode = getRoundingModeEnum(direction)
            result = mode != ROUNDING_MODES.UNDEFINED ? numberToRound.setScale(0, mode) : numberToRound
            break
        default:
            result = numberToRound
            break
    }

    return result
}

/**
 * Round a number with specified end points
 * @param down BigDecimal the lower end
 * @param up BigDecimal the higher end
 * @param numberToRound BigDecimal the number to be rounded
 * @param roundingDirection String rounding direction / mode
 * @return rounded number
 */
protected BigDecimal getRoundedNumber(BigDecimal down, BigDecimal up, BigDecimal numberToRound, String roundingDirection) {
    BigDecimal result = 0.0

    switch (roundingDirection) {
        case ROUNDING_MODES.UP:
            result = up
            break
        case ROUNDING_MODES.DOWN:
            result = down
            break
        case ROUNDING_MODES.HALF_UP:
            def avg = (down + up) / 2
            result = numberToRound >= avg ? up : down
            break
        case ROUNDING_MODES.HALF_DOWN:
            def avg = (down + up) / 2
            result = numberToRound <= avg ? down : up
            break
        default:
            result = numberToRound
            break
    }

    return result
}

/**
 * Get rounding mode in standard enum
 * @param direction String rounding direction / mode
 * @return rounded number
 */
protected int getRoundingModeEnum(String direction) {
    int mode

    switch (direction) {
        case ROUNDING_MODES.UP:
            mode = BigDecimal.ROUND_UP
            break
        case ROUNDING_MODES.DOWN:
            mode = BigDecimal.ROUND_DOWN
            break
        case ROUNDING_MODES.HALF_UP:
            mode = BigDecimal.ROUND_HALF_UP
            break
        case ROUNDING_MODES.HALF_DOWN:
            mode = BigDecimal.ROUND_HALF_DOWN
            break
        default:
            mode = ROUNDING_MODES.UNDEFINED
            break
    }

    return mode
}

/**
 * Get the nearest number that less than the number to be rounded and have the suffix in decimal part
 * @param numberToRound BigDecimal the number to be rounded
 * @param roundingTarget BigDecimal the suffix number to have
 * @return the nearest number. If there is no such number, return 0
 */
protected BigDecimal getRoundedDownDecimal(BigDecimal numberToRound, BigDecimal roundingTarget) {

    if (!numberToRound || numberToRound < 0 || roundingTarget >= 1) {
        return numberToRound
    }

    BigDecimal numberToRoundInt = trunc(numberToRound)
    BigDecimal decimalPart = (numberToRound - numberToRoundInt).setScale(DEFAULT_DECIMAL_DIGITS, BigDecimal.ROUND_HALF_UP)
    BigDecimal result = numberToRound

    if (decimalPart > roundingTarget) {
        result = numberToRoundInt + roundingTarget
    } else if (decimalPart < roundingTarget) {
        result = (numberToRoundInt < 1) ? 0.0 : numberToRoundInt - 1 + roundingTarget
    }

    return result
}

/**
 * Get the nearest number that greater than the number to be rounded and have the suffix in decimal part
 * @param numberToRound BigDecimal the number to be rounded
 * @param roundingTarget BigDecimal the suffix number to have
 * @return the nearest number
 */
protected BigDecimal getRoundedUpDecimal(BigDecimal numberToRound, BigDecimal roundingTarget) {

    if (!numberToRound || numberToRound < 0 || roundingTarget >= 1) {
        return numberToRound
    }

    BigDecimal numberToRoundInt = trunc(numberToRound)
    BigDecimal decimalPart = (numberToRound - numberToRoundInt).setScale(DEFAULT_DECIMAL_DIGITS, BigDecimal.ROUND_HALF_UP)
    BigDecimal result = numberToRound

    if (decimalPart > roundingTarget) {
        result = numberToRoundInt + 1 + roundingTarget
    } else if (decimalPart < roundingTarget) {
        result = numberToRoundInt + roundingTarget
    }

    return result
}

protected BigDecimal getRoundedDownToXxDecimal(BigDecimal numberToRound, BigDecimal roundingTarget) {
    return getRoundedToXxDecimal(numberToRound, roundingTarget, true)
}

protected BigDecimal getRoundedUpToXxDecimal(BigDecimal numberToRound, BigDecimal roundingTarget) {
    return getRoundedToXxDecimal(numberToRound, roundingTarget, false)
}

/**
 * Get the nearest number that is greater/less than the number to be rounded and have the suffix in decimal part<p>
 * The method is dedicated for the one hundredth's precision (SECOND_DECIMAL_PRECISION) only, currently two scenarios: ToX5Cents and ToX9Cents<p>
 * ToX5Cents - is rounding towards the closes .x5 value, ex 10.06 -> 10.15 (UP) or 10.05 (DOWN)<p>
 * ToX5Cents - is rounding towards the closes .x9 value, ex 10.10 -> 10.19 (UP) or 10.09 (DOWN)<p>
 *
 * @param numberToRound The number to be rounded as BigDecimal
 * @param roundingTarget The suffix number to have as BigDecimal
 * @param isRoundingDown The flag to differentiate between rounding down (true) or up (false) as boolean
 * @return the nearest number
 */
protected BigDecimal getRoundedToXxDecimal(BigDecimal numberToRound, BigDecimal roundingTarget, boolean isRoundingDown) {
    if (!numberToRound || numberToRound < 0) {
        return api.throwException("Number to be rounded is missing or less than / equal zero (number: " + numberToRound + ")")
    }

    BigDecimal result
    BigDecimal numberToRoundInt = trunc(numberToRound)
    BigDecimal targetPlusTenthPlace = roundingTarget + getTenthDecimalPlace(numberToRound)
    BigDecimal resultCorrection

    switch (true) {
        case roundingTarget == getHundredthDecimalPlace(numberToRound) && numberToRound.scale() == DEFAULT_DECIMAL_DIGITS:
            result = numberToRound
            break
        case targetPlusTenthPlace >= (numberToRound - numberToRoundInt):
            resultCorrection = isRoundingDown ? ONE_TENTH : 0.0
            result = numberToRoundInt + targetPlusTenthPlace - resultCorrection
            break
        default:
            resultCorrection = isRoundingDown ? 0.0 : ONE_TENTH
            result = numberToRoundInt + targetPlusTenthPlace + resultCorrection
            break
    }

    return result >= 0 ? result : numberToRound
}

/**
 * Get the nearest number that less than the number to be rounded and have the suffix in whole number part
 * @param numberToRound BigDecimal the number to be rounded
 * @param roundingTarget BigDecimal the suffix number to have
 * @return the nearest number
 */
protected BigDecimal getRoundedDownWhole(BigDecimal numberToRound, BigDecimal roundingTarget) {

    if (!numberToRound || numberToRound < 0 || roundingTarget < 1) {
        return numberToRound
    }

    BigDecimal result = numberToRound

    if (numberToRound > roundingTarget) {
        BigDecimal comparePart = getComparePart(numberToRound, roundingTarget)

        if (comparePart < roundingTarget) {
            numberToRound -= comparePart
            numberToRound--
            comparePart = getComparePart(numberToRound, roundingTarget)
        }

        numberToRound -= comparePart
        numberToRound += roundingTarget
        result = numberToRound
    } else if (numberToRound < roundingTarget) {
        result = 0.0
    }

    return trunc(result)
}

/**
 * Get the nearest number that greater than the number to be rounded and have the suffix in whole number part
 * @param numberToRound BigDecimal the number to be rounded
 * @param roundingTarget BigDecimal the suffix number to have
 * @return the nearest number
 */
protected BigDecimal getRoundedUpWhole(BigDecimal numberToRound, BigDecimal roundingTarget) {

    if (!numberToRound || numberToRound < 0 || roundingTarget < 1) {
        return numberToRound
    }

    BigDecimal result = numberToRound

    if (numberToRound > roundingTarget) {
        BigDecimal comparePart = getComparePart(numberToRound, roundingTarget)
        BigDecimal numberToRoundInt = trunc(numberToRound)

        if (comparePart > roundingTarget || (comparePart == roundingTarget && numberToRound - numberToRoundInt > 0)) {
            def compareDigits = comparePart.toString().length()
            def range = Math.pow(10, compareDigits)
            numberToRound /= range
            numberToRound++
            numberToRound *= range
            comparePart = getComparePart(numberToRound, roundingTarget)
        }

        numberToRound -= comparePart
        numberToRound += roundingTarget
        result = numberToRound

    } else if (numberToRound < roundingTarget) {
        result = roundingTarget
    }

    return trunc(result)
}

/**
 * Get the hundredth decimal place of any BigDecimal
 * @param number Any BigDecimal number
 * @return The hundredth decimal place of the number
 */
protected BigDecimal getHundredthDecimalPlace(BigDecimal number) {
    return number.setScale(2, RoundingMode.DOWN) - getTenthDecimalPlace(number) - trunc(number)
}

/**
 * Get the tenth decimal place of any BigDecimal
 * @param number Any BigDecimal number
 * @return The tenth decimal place of the number
 */
protected BigDecimal getTenthDecimalPlace(BigDecimal number) {
    return number.setScale(1, RoundingMode.DOWN) - trunc(number)
}

/**
 * Remove decimal part of a BigDecimal
 * @param number BigDecimal the number to be truncated
 * @return truncated number
 */
protected BigDecimal trunc(BigDecimal number) {
    return number.setScale(0, BigDecimal.ROUND_DOWN)
}

/**
 * Get compare part of a number
 * @param numberToRound BigDecimal the number contains part to be taken
 * @param roundingTarget BigDecimal the number to be compared
 * @return
 */
protected BigDecimal getComparePart(BigDecimal numberToRound, BigDecimal roundingTarget) {
    String trailingRegex = "\\.[0-9]+\$"
    String numberToRoundString = numberToRound.toString().replaceFirst(trailingRegex, "")
    String roundToNumberString = roundingTarget.toString().replaceFirst(trailingRegex, "")
    String comparePartString = numberToRoundString.substring(numberToRoundString.length() - roundToNumberString.length())

    BigDecimal comparePart = new BigDecimal(comparePartString)
    return comparePart
}