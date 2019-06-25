data class Cart(val id: Int) {
    var items: List<Item> = mutableListOf()

    val total: Money
        get () {
            return Money(items.map { it.price.defaultValue }.sum())
        }
}

data class Item(val id: Int, var price: Money, var category: ItemCategory, var weight: Float) {

    fun costForWeight(cost: Money): Money {
        return Money(weight / 1000 * cost.defaultValue)
    }
}

data class Money(var value: Float, val currency: String = USD) {
    companion object {
        const val USD = "USD"
        val ZERO = Money(0F, USD)
    }

    val defaultValue: Float
        get() {
            if (currency == USD) {
                return value
            }
            return 0F // TODO: Implement conversion
        }


    fun lessThan(amount: Money): Boolean {
        if (value <= amount.value && currency == amount.currency) {
            return true
        }
        return false
    }

    fun add(money: Money) {
        if (currency == money.currency) {
            value += money.value
        }
    }
}

enum class ItemCategory {
    C, Q
}

enum class ShippingOption {
    NORMAL {
        override fun costForItem(item: Item): Money {
            if (item.category == ItemCategory.C) {
                return Money(2.99F)
            }
            if (item.category == ItemCategory.Q) {
                return item.costForWeight(Money(2.99F))
            }
            return Money.ZERO
        }
    },
    PRIORITY {
        override fun costForItem(item: Item): Money {
            if (item.category == ItemCategory.C) {
                return Money(4.99F)
            }
            if (item.category == ItemCategory.Q) {
                return item.costForWeight(Money(2.99F))
            }
            return Money.ZERO
        }
    };

    abstract fun costForItem(item: Item): Money
}

val AMOUNT_FREE_SHIPPING = Money(100F)
val BASE_SHIPMENT_COST = Money(4.9F)

var carts: MutableList<Cart> = mutableListOf()

fun main() {

    val cart1 = Cart(1)
    cart1.items = listOf(
        Item(111, Money(2.5F), ItemCategory.C, 1F),
        Item(222, Money(25.8F), ItemCategory.Q, 2F)
    )
    carts.add(cart1)

    val shippingCost = calculateShippingCost(cart1, ShippingOption.NORMAL)

    print(shippingCost)
}

fun calculateShippingCost(cart: Cart, shippingOption: ShippingOption): Money {
    if (cart.total.lessThan(AMOUNT_FREE_SHIPPING)) {
        val shippingCost = BASE_SHIPMENT_COST
        for (item in cart.items) {
            shippingCost.add(shippingOption.costForItem(item))
        }
        return shippingCost
    }
    return Money.ZERO
}