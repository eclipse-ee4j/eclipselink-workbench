# EclipseLink Workbench

Workbench is a separate component from the EclipseLink runtime – it lets you graphically
configure descriptors and map your project. Workbench can verify the descriptor options,
access the data source (either a database or an XML schema), and create the database schema.
Using Workbench, you can define EclipseLink descriptors and configurations without using code.

The Workbench is primarily for developing when using the native EclipseLink API, MOXy, or EIS.
When using JPA, other development tools such as Eclipse Dali, or Oracle JDeveloper can be used.

## Requirements

 * JDK 1.8
 * JAVA_HOME environment variable set to JDK 1.8
 
## Documentation

 * [Workbench User Documentation](https://wiki.eclipse.org/Using_Workbench_(ELUG))
 * [EclipseLink Documentation](https://www.eclipse.org/eclipselink/documentation/)

## Discussions &amp; News

 * [User mailing list](https://accounts.eclipse.org/mailing-list/eclipselink-users)
 * [User Forum](https://www.eclipse.org/forums/index.php?t=thread&frm_id=111)
 * [Developer Mailing List](https://accounts.eclipse.org/mailing-list/eclipselink-dev)

## Building

`mvn install` produces ready to run Workbench installation in `distribution/target/stage` folder

## License

EclipseLink Workbench is available under the Eclipse Public License, v. 2.0
or the Eclipse Distribution License, v. 1.0.
