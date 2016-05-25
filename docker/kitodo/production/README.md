[![Docker Pulls](https://img.shields.io/docker/pulls/kitodo/production.svg)](https://hub.docker.com/r/kitodo/production/) [![Docker Stars](https://img.shields.io/docker/stars/kitodo/production.svg)](https://hub.docker.com/r/kitodo/production/)

![Kitodo logo](https://raw.githubusercontent.com/UB-Mannheim/kitodo-production/docker/Goobi/newpages/images/template/kitodo-640x186.png)

# What is Kitodo?

Kitodo (_Key to digital objects_) is a collection of programs used for digital objects in libraries.
Kitodo.Production is the workflow component and supports the whole process from scanning books until exporting of the data needed for the web presentation.

Kitodo is developed by [Kitodo. Key to digital objects e. V.](http://www.kitodo.org/)
The Docker images were built by [Mannheim University Library](https://en.wikipedia.org/wiki/Mannheim_University_Library).

# Usage

Run the latest Docker images like this:

    docker run --detach=true --name kitodo-database kitodo/database:latest
    docker run --detach=true -p 8888:8080 --name kitodo-production \
               --link kitodo-database:mysql kitodo/production:latest

Then Kitodo.Production can be accessed at http://localhost:8888/kitodo.

# Code and User Feedback

This image is based on code available at [GitHub](https://github.com/UB-Mannheim/kitodo-production/tree/docker). If you have any problems with or questions about this image, you can contact us through a [GitHub issue](https://github.com/UB-Mannheim/kitodo-production/issues).
