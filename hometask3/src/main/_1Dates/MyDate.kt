package _1Dates

data class MyDate(val year: Int, val month: Int, val dayOfMonth: Int) {
    infix operator fun compareTo(other: MyDate): Int = when {
        year != other.year -> year - other.year
        month != other.month -> month - other.month
        else -> dayOfMonth - other.dayOfMonth
    }

    infix operator fun plus(timeInterval: TimeInterval): MyDate {
        val data = addTimeIntervals(timeInterval, timeInterval.number)
        timeInterval.number = 1
        return data
    }
}

class DateRange(val start: MyDate, val endInclusive: MyDate) {
    infix operator fun contains(date: MyDate): Boolean = start <= date && date <= endInclusive

    operator fun iterator(): Iterator<MyDate> = DataRangeIterator()

    private inner class DataRangeIterator() : Iterator<MyDate> {
        var nextDate = this@DateRange.start

        override fun hasNext(): Boolean = nextDate <= this@DateRange.endInclusive

        override fun next(): MyDate {
            val date = nextDate
            nextDate = nextDate.nextDay()
            return date
        }
    }
}

operator fun MyDate.rangeTo(other: MyDate): DateRange = DateRange(this, other)

enum class TimeInterval {
    DAY,
    WEEK,
    YEAR;
    var number = 1;

    infix operator fun times(i: Int): TimeInterval {
        number *= i
        return this
    }
}