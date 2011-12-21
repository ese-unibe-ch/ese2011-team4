__CalPal v1.0__

Please read the following descriptions, before using __CalPal__!

# Description
What is __CalPal__? __CalPal__ is an calendar application which helps you to plan your day,week, month and year. Basicly it is a calendar, which is able to save events which occur in your life. __CalPal__ is not just a simple calendar app, which	can just save events, it can do a lot more!

The Program has following features:

* Register yourself on the page
* Save events (with a description, starting/ending date, location (is also shown on Google Maps), privacy, location and of course a name)
* Edit events with a sweet datepicker
* Add locations (if there is a conflict with them, you get warned)
* Repeat events (daily, weekly, monthly, yearly or any other period, you prefer)
* Save events from other users, into your calendar (Note: If the creator of the event deletes or edits it, the result will be also in your calendar!)
* Comment on events of other users
* See, who joined which calendar
* Add users to your favorite list (makes easier to follow other users)
* Search for users by name and e-mail, calendars and events
* Collision of events will be shown (timeoverlapping with two or more events)
* Print out the whole calendar or just the event, you want
* Reminder, which reminds you of events via e-mail
* A complete user profile (user can decide whether everything is public or not)
* Send messages to other users

# Installation
Okay, there is one thing you should do. Run the following command within the folder of the application: `play dependencies calendar`. Beside of that there are no special things to do. Just copy all files in ONE folder. Do not rename anything in these folders! The folder calendar and the Play! files have to be in the same folder otherwise you can not start __CalPal__!

## How to run __CalPal__?
Since __CalPal__ runs with the play! framework (see link below), you have to run this application via command-lines. So you have to open up the os specific prompt and switch to the folder, where the application is in (e.g. if your folder is `C:\Users\Username\CalPal` you type in `cd users/username/CalPal`).

Now you can run the application with the following command: play run calendar. There should be a tex like this:
             _            _
       _ __ | | __ _ _  _| |
      | '_ \| |/ _' | || |_|
      |  __/|_|\____|\__ (_)
      |_|            |__/
     
      play! 1.2.3, http://www.playframework.org
     
      Ctrl+C to stop
     
     Listening for transport dt_socket at address: 8000
     16:52:16,823 INFO  ~ Starting C:\Users\Username\Java\Play\calendar
     16:52:16,826 INFO  ~ Module cobertura is available (C:\Users\Username\Java\Play\
     calendar\modules\cobertura-2.4)
     16:52:16,827 INFO  ~ Module secure is available (C:\Users\Username\Java\Play\mod
     ules\secure)
     16:52:17,249 WARN  ~ You're running Play! in DEV mode
     16:52:17,315 INFO  ~ Listening for HTTP on port 9000 (Waiting a first request to
      start) ...

> Here you can see that the program is waiting on port 9000. So open up your webbrowser and go to the following site: localhost:9000 You should see the login page of __CalPal__ and the promp window should show this:

     16:57:13,996 INFO  ~ Connected to jdbc:h2:mem:play;MODE=MYSQL
     16:57:14,928 INFO  ~ Application 'calendar' is now started !
     16:57:15,101 INFO  ~ Loading intial data


Now you can register on the page or use our provided demo data (below), to login. After registration you get back to the login page.

## Test __CalPal__
If you have logged in successfully, you can test the application. You get redirected to a list of all your calendars (if you did not use the demo data, you will not have any). Here you can see your favorites and your actions. You can add a new calendar, add a new location, edit your profile or search for other users, calendars and events. You have also the ability to see all Users and Locations by clicking on one link in the upper navigation bar. So feel free to test our 5 week project!
	
## Demo data
> _Jack_  
> email:          jack.vincennes@lapd.com  
> password:       secret  
	
> _Bud_  
> email:          bud.white@lapd.com  
> password:       secret  
    
> _Ed_  
> email:          ed.exley@lapd.com  
> password:       secret  
	
## play! framework
[playframework.org](http://www.playframework.org/ "Official webpage for the play framework")

# Contact us
## e-mail
> ese-team4@iam.unibe.ch  
> michu.baertschi@students.unibe.ch  
> joel.krebs@students.unibe.ch  
> simpal.kumar@students.unibe.ch  
> trixter16@web.de  
> roger.stebler@students.unibe.ch  
		
## Github
> https://github.com/mbaertschi  
> https://github.com/jokr  
> https://github.com/simpalK  
> https://github.com/Trixt0r  
> https://github.com/rstebler  
	
## Twitter
> http://twitter.com/#!/ese_unibe_ch  
> http://twitter.com/#!/mbaertschi89  
> http://twitter.com/#!/jomikr  
> http://twitter.com/#!/SimpalK  
> http://twitter.com/#!/Trixt0r90  
> http://twitter.com/#!/rostebler  
