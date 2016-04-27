# Selenium server with video recording feature
This project is created to provide native video recording support for Selenium Grid and designed to be used with [docker-selenium](https://github.com/sskorol/docker-selenium) project.

It uses [avconv](https://libav.org/avconv.html) tool to produce mp4 output. The entire recording process is managed on selenium session level.

`VideoInfo` entity is designed to supply required video options to be able to control output paths, quality and frame rate. As there's no easy way to extend official selenium sources, you should provide exactly the same entity on a client level and pass corresponding info in json format as a part of `DesiredCapabilities`.

To be able to handle when actual recording is completed, all videos are published into `tmp` folder, which is relative to the provided output. Note that you should map corresponding volumes on Docker level to be able to correctly consume mp4 output. E.g. for `docker-compose.yml` it may look like the following:

```
firefoxnode:
 image: sskorol/node-firefox-debug:2.53.0
 volumes:
 - ~/work:/e2e/uploads
 - ~/work/tmp:/e2e/uploads/tmp
```

To build this project use the following command:

```
mvn clean install
```

To use newly created jar with [docker-selenium](https://github.com/sskorol/docker-selenium), just copy it into corresponding [Base/lib](https://github.com/sskorol/docker-selenium/tree/master/Base/lib) folder and rebuild required chain of images.