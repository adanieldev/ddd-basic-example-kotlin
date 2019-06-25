data class Cart(val id: Int) {
    var items: List<Item> = mutableListOf()

    val total: Float
        get () {
            return items.map { it.price }.sum()
        }
}

data class Item(val id: Int, var price: Float, var cat: String, var weight: Float)

var carts: MutableList<Cart> = mutableListOf()

fun main() {

    val cart1 = Cart(1)
    cart1.items = listOf(
        Item(111, 2.5F, "C", 1F),
        Item(222, 25.8F, "Q", 2F)
    )
    carts.add(cart1)

    val shippingCost = process(1, 1)

    print(shippingCost)
}

fun process(cartId: Int, option: Int): Float {
    val cart = carts.first { it.id == cartId }

    var cost = 0F
    if (cart.total <= 100) {
        cost = 4.99F

        if (option == 1) {
            for (i in cart.items) {
                if (i.cat == "C") {
                    cost += 2.99F
                }
                if (i.cat == "Q") {
                    cost += i.weight / 1000 * 2.99F
                }
            }
        }
        if (option == 2) {
            for (i in cart.items) {
                if (i.cat == "C") {
                    cost += 4.99F
                }
                if (i.cat == "Q") {
                    cost += i.weight / 1000 * 2.99F
                }
            }
        }

    }
    return cost
}