# Selenium server with video recording feature
This project is created to provide native video recording support for Selenium Grid and was initially designed to be used with [docker-selenium](https://github.com/sskorol/docker-selenium) project. See details in related [article](http://qa-automation-notes.blogspot.com/2016/04/docker-selenium-and-bit-of-allure-how.html).

As Docker will be available on Windows soon, initial approach was extended for supporting corresponding OS. Technically, now you can use it on Windows even without Docker.

[ffmpeg](https://ffmpeg.org) tool is used to produce mp4 output. The entire recording process is managed on selenium session level.

`VideoInfo` entity is designed to supply required video options to be able to control output paths, quality and frame rate. As there's no easy way to extend official selenium sources, you should provide exactly the same entity on a client level and pass corresponding info in json format as a part of `DesiredCapabilities`.

To be able to handle when actual recording is completed, all videos are published into `tmp` folder, which is relative to the provided output. Note that you should map corresponding volumes on Docker level to be able to correctly consume mp4 output. E.g. for `docker-compose.yml` it may look like the following:

```
firefoxnode:
 image: sskorol/node-firefox-debug:2.53.0
 volumes:
 - ~/work:/e2e/uploads
 - ~/work/tmp:/e2e/uploads/tmp
```

On Windows it's not required to map anything. Temporary folder will be created automatically, if it doesn't exist yet.

To build this project use the following command:

```
mvn clean install
```

To use newly created jar with [docker-selenium](https://github.com/sskorol/docker-selenium), just copy it into corresponding [Base/lib](https://github.com/sskorol/docker-selenium/tree/master/Base/lib) folder and rebuild required chain of images.

[![demo](http://img.youtube.com/vi/f73ea4-RVHo/0.jpg)](http://www.youtube.com/watch?v=f73ea4-RVHo)
