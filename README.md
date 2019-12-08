# QR Sign

An Android app that can cryptographically sign physical documents using QRCode.

## Overview

Messages on a digital device can easily be made verifiable by signing them with a key pair. A signed message would be accompanied by a digital signature for verification with the public key from a credible source.

However, it is harder to verify physical documents, traditional means of stamps and handwritten signatures are difficult to verify and frauds are not easily noticeable.

QR Sign uses the ED25519 signature cryptography to generate a key pair for the average person to easily sign any physical documents. The public key is placed on a authoritative site, such as a Facebook page or a basic web page for the verification process to take place. The private key is kept on the phone and can be synced onto the Cloud. The private key is used to sign the message with a date and the public key location URL. The result is converted into a QR Code graphics with textual description of the code for users to scan. The QR Code should be placed alongside the physical material with the textual description intact for visual verification.

The user is required to scan the code and compare the message on the physical material to verify that the code is correctly used for the right material. It is important that the physical document do not strip away the message and date of notice as the app can only verify whether the QR Code is a valid signature.