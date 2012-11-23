# Simple Open Social Server
This project contains a simple and incomplete implementation of the Open Social API. It can be used to mock or imitate a 'real' Open Social Server.

It is written in Scala using [Scalatra][scalatra] and [Lift-json][lift-json].

## Running the server

    maven jetty:run

Optional: Install the [Zinc server][zinc] for faster compilation `brew install zinc; zinc -start`.

# REST interface

    POST: /persons                      // adds a person
    POST: /persons/[uid]/groups         // adds a group to a person with uid
    DELETE: /persons/[uid]              // deletes a person with uid
    DELETE: /persons/[uid]/groups/[gid] // deletes a group with gid for person with uid

[scalatra]: http://www.scalatra.org/
[lift-json]: https://github.com/lift/lift/tree/master/framework/lift-base/lift-json/
[zinc]: https://github.com/typesafehub/zinc
