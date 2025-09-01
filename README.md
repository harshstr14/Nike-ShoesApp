# Nike-ShoesApp

An Android application for browsing and purchasing Nike shoes.

## Description

This Android application allows users to browse a catalog of Nike shoes, add items to a cart, manage their favorite items, place orders, and manage their profile. The app integrates with Firebase for user authentication, real-time database storage, and image management. The application uses the Cloudinary service to upload and manage user profile pictures.

## Features and Functionality

-   **User Authentication:**
    -   Sign-up and Sign-in with Email/Password using Firebase Authentication. (`SignIn.kt`, `SignUp.kt`)
    -   Google Sign-in integration using Firebase. (`GoogleSignInManager.kt`, `SignIn.kt`, `SignUp.kt`)
    -   Password recovery via email. (`RecoveryPassword.kt`)
    -   Persistent login using SharedPreferences and Google Sign-in. (`MainActivity.kt`)
-   **Product Browsing:**
    -   Browse shoes by category (All, Air Jordan 1, Air Force 1, Dunk, Blazer, V2K). (`Home.kt`, `CategoryAdapter.kt`)
    -   Display of shoe images, names, types, and prices. (`ShoesAdapter.kt`, `item_shoes.xml`)
    -   Image slider for featured products on the home screen. (`Home.kt`)
-   **Product Details:**
    -   Detailed view of each shoe, including multiple images, name, type, price, description, and product details. (`Details.kt`, `activity_details.xml`)
    -   Selection of shoe size. (`Details.kt`, `SizeCategoryAdapter.kt`, `item_sizecategory.xml`)
-   **Shopping Cart:**
    -   Add shoes to cart from the product details page. (`Details.kt`)
    -   View and manage items in the cart, including quantity adjustments. (`MyCart.kt`, `MyCartAdapter.kt`, `item_cart.xml`)
    -   Real-time updates of subtotal, shopping fee, and total cost. (`MyCart.kt`)
    -   Delete Items from cart (`MyCartAdapter.kt`)
-   **Checkout Process:**
    -   Display subtotal, shipping fee, and total cost. (`CheckOut.kt`)
    -   Address input and storage in Firebase Realtime Database. (`CheckOut.kt`)
    -   Credit card details input and secure storage. (`CheckOut.kt`)
    -   Payment simulation with transaction ID and order ID generation. (`CheckOut.kt`)
    -   Order confirmation notification. (`CheckOut.kt`)
-   **Favorite Items:**
    -   Add and remove shoes from a favorites list. (`Details.kt`, `Favourite.kt`, `FavouriteAdapter.kt`, `item_favourite.xml`)
    -   Display of favorite items in a grid layout. (`Favourite.kt`)
-   **Order History:**
    -   Display a list of past orders with order ID, date, time, and total amount. (`Orders.kt`, `OrdersAdapter.kt`, `item_order.xml`)
    -   Detailed view of each order, including product list and shipping address. (`OrdersDetails.kt`, `activity_orders_details.xml`)
-   **User Profile:**
    -   View and update profile information, including name, email, and phone number. (`Profile.kt`, `activity_profile.xml`)
    -   Upload and update profile picture using Cloudinary. (`Profile.kt`, `MyApp.kt`)
-   **Search Functionality:**
    -   Search for shoes by name. (`Search.kt`, `SearchAdapter.kt`, `custom_search_view.xml`, `item_search.xml`)
-   **Navigation:**
    -   Bottom navigation for quick access to Home, Favorites, Cart, Notifications, and Profile. (`Home.kt`, `activity_home.xml`)
    -   Navigation drawer for accessing Profile, Home, Cart, Favorites, Orders, Notifications, and Sign Out. (`Home.kt`, `activity_home.xml`)
-   **Splash Screens:**
    -   Introductory splash screens. (`Screen1.kt`, `Screen2.kt`, `Screen3.kt`)
    
## Technology Stack

-   **Kotlin:** Primary programming language.
-   **Android SDK:** For building the Android application.
-   **Firebase:**
    -   Firebase Authentication: For user authentication.
    -   Firebase Realtime Database: For storing user data, product information, cart details, and order history.
-   **Cloudinary:** For image management and storage.
-   **Picasso:** For image loading and caching.
-   **AndroidX Libraries:**
    -   AppCompat: For backward compatibility.
    -   RecyclerView: For displaying lists of data.
    -   ConstraintLayout: For creating flexible layouts.
    -   Material Components: For using Material Design elements.
    -   EdgeToEdge: For immersive experiences with edge-to-edge content.
-   **Gradle:** For dependency management and building the application.

## Prerequisites

-   Android Studio installed on your development machine.
-   A Firebase project with Realtime Database enabled.
-   A Cloudinary account for image storage.

## Installation Instructions

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/harshstr14/Nike-ShoesApp.git
    ```

2.  **Open the project in Android Studio.**

    *   Launch Android Studio.
    *   Click on "Open an Existing Project".
    *   Navigate to the cloned repository and select the `Nike-ShoesApp` folder.

3.  **Configure Firebase:**

    *   Go to your Firebase project console.
    *   Add a new Android app to your Firebase project.
    *   Download the `google-services.json` file and place it in the `app/` directory of your project.
    *   Ensure that the necessary Firebase dependencies are added to your `build.gradle` files.

        ```gradle
        // Top-level build.gradle
        buildscript {
            dependencies {
                classpath("com.google.gms:google-services:4.4.0")
            }
        }

        // app/build.gradle
        plugins {
            id("com.google.gms.google-services")
        }

        dependencies {
            implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
            implementation("com.google.firebase:firebase-database-ktx:20.3.0")
            // other dependencies
        }
        ```

4.  **Configure Cloudinary:**

    *   Obtain your Cloudinary cloud name, API key, and API secret from your Cloudinary dashboard.
    *   Initialize Cloudinary in the `MyApp.kt` file:

        ```kotlin
        package com.example.nike

        import android.app.Application
        import com.cloudinary.android.MediaManager

        class MyApp: Application() {
            override fun onCreate() {
                super.onCreate()

                val config = HashMap<String, String>()
                config["cloud_name"] = "your_cloud_name" // Replace with your cloud name
                config["api_key"] = "your_api_key" // Replace with your API key
                config["api_secret"] = "your_api_secret" // Replace with your API secret
                MediaManager.init(this,config)
            }
        }
        ```

5.  **Build and run the application:**

    *   Connect an Android device or start an emulator.
    *   Click on "Run" in Android Studio to build and run the application on your device/emulator.

## Usage Guide

1.  **Launch the application.**

    *   The app starts with a series of splash screens (`Screen1.kt`, `Screen2.kt`, `Screen3.kt`).

2.  **Sign-in/Sign-up:**

    *   If you don't have an account, click on the "Sign Up" button to create a new account. (`SignUp.kt`)
    *   If you already have an account, enter your email and password and click on the "Sign In" button. (`SignIn.kt`)
    *   You can also use Google Sign-in by clicking on the "Google Sign-in" button. (`SignIn.kt`, `SignUp.kt`)

3.  **Browse shoes:**

    *   Once signed in, you'll be taken to the home screen (`Home.kt`).
    *   Browse shoes by category using the category RecyclerView.
    *   Use the image slider to view featured products.

4.  **View product details:**

    *   Click on a shoe to view its details. (`Details.kt`)
    *   Select your shoe size and click on the "Add to Cart" button to add the shoe to your cart.
    *   Add item to your favourite list

5.  **Manage cart:**

    *   Click on the cart icon in the bottom navigation bar to view your cart. (`MyCart.kt`)
    *   Adjust the quantity of items in your cart.
    *   Remove items from your cart.

6.  **Checkout:**

    *   Click on the "Checkout" button to proceed to the checkout page. (`CheckOut.kt`)
    *   Enter your shipping address and credit card details.
    *   Click on the "Payment" button to confirm your order.

7.  **View order history:**

    *   Click on the "Orders" item in the navigation drawer to view your order history. (`Orders.kt`)

8.  **Manage profile:**

    *   Click on the "Profile" item in the navigation drawer to view and update your profile information. (`Profile.kt`)
    *   Upload a new profile picture by clicking on the camera icon.

9.  **Sign out:**

    *   Click on the "Sign Out" item in the navigation drawer to sign out of the application.

## API Documentation

This project uses Firebase Realtime Database. Refer to the official Firebase documentation for API details:

-   [Firebase Realtime Database](https://firebase.google.com/docs/database)

This project uses Cloudinary service. Refer to the official Cloudinary documentation for API details:

-   [Cloudinary](https://cloudinary.com/documentation)

## Contributing Guidelines

Contributions are welcome! To contribute to this project, follow these steps:

1.  Fork the repository.
2.  Create a new branch for your feature or bug fix.
3.  Make your changes and commit them with descriptive commit messages.
4.  Test your changes thoroughly.
5.  Submit a pull request to the `master` branch.

## License Information

No license specified. All rights reserved.

## Contact/Support Information

For questions, bug reports, or feature requests, please contact the repository owner through GitHub.
