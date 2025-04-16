# Config Server
Config server can be started in 2 ways
- As is, by simply starting the app. It will read the configurations from [this](https://github.com/EmirKapic/nwt_config_server/tree/main)
github repository.
- By using local configurations. This can be done by changing the `spring.profiles.active` property from `deployed`
to `native`, and inserting the path to your local config. <br>
This is useful in case you want to change/test the config without having to push changes to the config repository.