# UrgentCare

# Overview
Android App that alerts registered volunteers about an accident in case a victim gets one.
A Victim or any other person at the scene send notifications to all registered volunteers.
The notifications can be in form of plain text or an image of the current situation at the scene.
When a volunteer recieves the notification, he can reply back and also gets a Map View of the location of the accident.
The volunteer can also have a group chat with the registered volunteers as they interact on the current situation at the scene.

# Development
The App strongly uses the Firebase API to implement alot of functionalities including the chat which uses the Firebase Realtime Database.
Firebase Cloud Messaging (FCM) was used in implementing the notifications.
The Cloud Functions that handle the notifications were all written in JavaScript.
Firebase Authentication was used to implement user logins and registrations.
Firebase Realtime Database stored most of the information about users and other data required to implement the App's functionalities

# Credits
Credit to the team work effort by the four members; Peter, Sheilah, Diana and Agnes

