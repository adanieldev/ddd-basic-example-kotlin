// ---- Classes ----

data class Cart(val id: Int) {
    var items: List<Item> = mutableListOf()

    private val freeShippingThreshold = Money(100.0)

    val total: Money
        get () {
            val totalInUSD = items.map { it.price.amountInUSD }.sum()
            return Money(totalInUSD)
        }

    val qualifiesForFreeShipping: Boolean
        get() { return total.amountInUSD > freeShippingThreshold.amountInUSD }
}



data class Item(val id: Int, var price: Money, var cat: Category, var weight: Weight) {
    enum class Category { C, Q }
}



data class Weight(private val magnitude: Double, val unit: Unit = Unit.kg) {
    enum class Unit {
        g, kg, lb, Ton;
        companion object { const val kilogramsToPoundsRatio = 2.56 }

        val conversionFromKilogramRatio: Double
            get() {
                return when (this) {
                    g -> 1000.0
                    kg -> 1.0
                    lb -> 2.56
                    Ton -> 0.001
                }
            }
    }

    val magnitudeInKilograms: Double
        get() { return magnitude / unit.conversionFromKilogramRatio }

    fun getMagnitudeIn(resultUnit: Unit): Double {
        return magnitudeInKilograms * resultUnit.conversionFromKilogramRatio
    }
}



enum class ShippingOption {
    REGULAR, EXPRESS
}



data class Money(private val amount: Double, val currency: Currency = Currency.USD) {
    companion object {
        val zero = Money(0.0)
    }

    val amountInUSD: Double
        get() { return amount / currency.conversionFromUSDRatio }

    fun getAmmountIn(currency: Currency): Double {
        return amountInUSD * currency.conversionFromUSDRatio
    }
}



enum class Currency {
    USD, BOB;

    val conversionFromUSDRatio: Double
        get() {
            return when (this) {
                USD -> 1.0
                BOB -> 6.96
            }
        }
}


// ---- Functions ----

fun main(args: Array<String>) {

    var carts: MutableList<Cart> = mutableListOf()

    val cart1 = Cart(1)
    cart1.items = listOf(
            Item(111, Money(2.5), Item.Category.C, Weight(1.0)),
            Item(222, Money(25.8), Item.Category.Q, Weight(2.0))
    )
    carts.add(cart1)

    val shippingCost = calculateShippingCostOf(cart1, ShippingOption.REGULAR)

    print(shippingCost)
}

fun calculateShippingCostOf(cart: Cart, shippingOption: ShippingOption): Money {
    if (cart.qualifiesForFreeShipping) { return Money.zero }
    var moneyAmountInUSD = 4.99
    moneyAmountInUSD += cart.items.map { item ->
        when (item.cat) {
            Item.Category.C ->
                when (shippingOption) {
                    ShippingOption.REGULAR -> 2.99
                    ShippingOption.EXPRESS -> 4.99
                }
            Item.Category.Q -> item.weight.getMagnitudeIn(Weight.Unit.Ton) * 2.99
        }
    }.sum()
    return Money(moneyAmountInUSD)
}
