OVERVIEW

This application is 1 of 2 modules that make the project complete.

Android applications powered by Bluetooth Low Energy device to keep track of school going child by respective parents.

The system consists of two main components that complement each other; Bus module and the Parent module. Bus module, an Android Application detects the child wearing Eddystone capable BLE tags and updates the status of the child on the data server. Parent module, another Android application helps parent to know about the whereabouts of the child.

System’s primary goal is to address issue regarding child safety. School going children are linked with respective parents to get real time notifications on their android device. The Android app should be used by parent to track status of child who is uniquely addressed by a BLE beacon.

Bus Module Bus Android Application installed on an Android device with BLE Compatibility reads the Eddystone-TLM frame being transmitted by the Eddystone capable BLE tags. Bus Application will automatically detect the tag once the child is in the specified range. The child will be associated with the Bluetooth address of the BLE tag that the child is wearing. Once the child is detected, the Bus Application will send the data to the database server.



