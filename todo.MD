TODO
====

User Interface
--------------
* Refactor MainController (Naming !!!)
* Add thresholds on chart widget that change current value background color
* Feature to add a widget to the monitoring tab from a menu item/toolbar button
* Feature to persists recently added server, then add a dropdown to the connection dialog
* Settings to change default refresh rate per kpi
* Node widget
* Indices widget
* Query widget
* App Icon
* Widget Icon

Services
--------
* Add some comments in code
* Handle cancel outstanding request in KpiProvider when stopping provider. This seems difficult without handling low
level io. see http://spray.io/documentation/1.2.3/spray-can/common-behavior/#commonbehavior
* Handle connection error in KpiProvider and report them to requester
* Expose refresh interval to Kpi requester
* Refactor Stat classes using [spray-json-infer](https://github.com/jpthomasset/spray-json-infer)

Tests
-----
* Add ui test, there is none !

Other
-----
* Add doc & screenshots in readme.MD