# SecureMyAccount

Visit the plugin page [here](https://dev.bukkit.org/bukkit-plugins/securemyaccount/).

Inspired by GitHub and Google, here is a Bukkit plugin to use two factor authentication. Your Mojang/Minecraft
account is protected by a single password or login token. What happens if your account gets stolen? Therefore this
plugin exists.

The password is time based and generated by your smartphone completely independent from your Mojang account and your
computer. This process is also known as 2FA.

## Features

* Many other plugins like this sends a clickable link to Google for the QR code. This plugin doesn't do that. 
The code is generated rendered only on the server for increased privacy and security
* Protect selected commands
* Provides a better security to your server
* Displays the QR code on a minecraft map
* User friendly. Just scan the QR code with your smartphone!
* A session will be valid for certain time period so you don't have to enter your password again.
* No other plugins required

## Commands
Command |  Description
----------------|--------------
/register | Requests a new secret key
/login < code > | Starts a new session to prove your identity

## Permissions
Permission Node |  Description
----------------|--------------
securemyaccount.command.enable | Permission to invoke the request command
securemyaccount.command.start | Permission to start a new session
securemyaccount.command.protect | Protected player accounts

## Images

![Ingame map QR code](https://i.imgur.com/9YuekuKl.png)
![App code generation](https://i.imgur.com/HWNR8SK.png)
![inventory pin](https://i.imgur.com/JCmmMPOm.png)

## Requirements

2 Factor App on your phone, desktop or offline (specialized hardware tokens).

### Apps (Open-Source only)

IOS
* Authenticator [AppStore](https://itunes.apple.com/us/app/authenticator/id766157276)
* FreeOTP [AppStore](https://itunes.apple.com/us/app/freeotp-authenticator/id872559395)

Android
* andOTP [F-Droid](https://f-droid.org/en/packages/org.shadowice.flocke.andotp/)
    [PlayStore](https://play.google.com/store/apps/details?id=org.shadowice.flocke.andotp)
* Yubico Authenticator [F-Droid](https://play.google.com/store/apps/details?id=com.yubico.yubioath)
    [PlayStore](https://play.google.com/store/apps/details?id=com.yubico.yubioath)
    * Requires YubiKey hardware token
* OnlyKey U2F [PlayStore](https://play.google.com/store/apps/details?id=to.crp.android.onlykeyu2f)
    * Requires OnlyKey hardware token

Desktop (Linux, Mac, Windows):
* YubiKey Authenticator [Download](https://www.yubico.com/products/services-software/download/yubico-authenticator/)
    * Requires YubiKey hardware token
* NitroKey App [Download](https://www.nitrokey.com/download)
    * Requires Nitrokey hardware token
* OnlyKey App
    * Requires OnlyKey hardware token
