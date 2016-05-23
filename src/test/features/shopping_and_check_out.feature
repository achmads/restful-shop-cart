Feature: Shop Cart
    To allow consumer to buy any item and checkout their invoice within cart.
    There are concurrent visitors doing shopping.
    For each checkout with discount, 'welcome' coupon is given which is a fixed value, USD 3,
    while another coupon, 'ramadhan', discount is calculated by percentage, 5 % of total purchase.
    Each product is identified by short-name that represents their ID.
    To make it simple, the session is identified by visitor name to maintain their state.

    Scenario: Do shopping and checkout the cart
        Given database preparation
        And visitors pick their items:
            | achmad | 1 | tv           | 100  |
            | achmad | 2 | wall-clock   | 10   |
            | achmad | 1 | sound-system | 350  |
            | ilham  | 2 | calculator   | 0.75 |
            | ilham  | 1 | hand-watch   | 100  |
            | ilham  | 1 | ehair-cutter | 120  |
            | nasir  | 1 | tv           | 100  |
            | nasir  | 2 | wall-clock   | 100  |
            | nasir  | 1 | sound-system | 350  |
            | budi   | 2 | calculator   | 0.75 |
            | budi   | 1 | hand-watch   | 100  |
            | budi   | 1 | ehair-cutter | 175  |
        Then achmad checks out the cart and got USD 470 invoice
        And ilham checks out the cart and got USD 221.5 invoice
        And nasir checks out the cart with coupon welcome and got USD 467 invoice
        And budi checks out the cart with coupon ramadhan and got USD 210.425 invoice
        And achmad cancel the sound-system
        And achmad checks out the cart and got USD 120 invoice

    Scenario: Do shopping with some non existent product and checkout the cart
        Given database preparation
        And visitors pick non existent items:
            | achmad | 1 | xxx        | 100 |
        Then achmad checks out the cart and got nothing
