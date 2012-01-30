/*
    griffon-oraclekv plugin
    Copyright (C) 2012 Andres Almiray

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * @author Andres Almiray
 */
class OraclekvGriffonPlugin {
    // the plugin version
    String version = '0.1'
    // the version or versions of Griffon the plugin is designed for
    String griffonVersion = '0.9.5-SNAPSHOT > *'
    // the other plugins this plugin depends on
    Map dependsOn = [:]
    // resources that are included in plugin packaging
    List pluginIncludes = []
    // the plugin license
    String license = 'GNU Affero GPL 3.0'
    // Toolkit compatibility. No value means compatible with all
    // Valid values are: swing, javafx, swt, pivot, gtk
    List toolkits = []
    // Platform compatibility. No value means compatible with all
    // Valid values are:
    // linux, linux64, windows, windows64, macosx, macosx64, solaris
    List platforms = []
    // URL where documentation can be found
    String documentation = ''
    // URL where source can be found
    String source = ''

    List authors = [
        [
            name: 'Andres Almiray',
            email: 'aalmiray@yahoo.com'
        ]
    ]
    String title = 'Oracle NoSQL support'
    String description = '''
The Oraclekv plugin enables lightweight access to [Oracle NoSQL][1] datastores.
This plugin does NOT provide domain classes nor dynamic finders like GORM does.

Usage
-----
Upon installation the plugin will generate the following artifacts in `$appdir/griffon-app/conf`:

 * OraclekvConfig.groovy - contains the store definitions.
 * BootstrapOraclekv.groovy - defines init/destroy hooks for data to be manipulated during app startup/shutdown.

A new dynamic method named `withOraclekv` will be injected into all controllers,
giving you access to an `oraclekv.store.KVStore` object, with which you'll be able
to make calls to the store. Remember to make all calls to the store off the EDT
otherwise your application may appear unresponsive when doing long computations
inside the EDT.
This method is aware of multiple stores. If no storeName is specified when calling
it then the default store will be selected. Here are two example usages, the first
queries against the default store while the second queries a store whose name has
been configured as 'internal'

	package sample
	class SampleController {
	    def queryAllDataSources = {
	        withOraclekv { storeName, store -> ... }
	        withOraclekv('internal') { storeName, store -> ... }
	    }
	}
	
This method is also accessible to any component through the singleton `griffon.plugins.oraclekv.OraclekvConnector`.
You can inject these methods to non-artifacts via metaclasses. Simply grab hold of a particular metaclass and call
`OraclekvConnector.enhance(metaClassInstance)`.

Configuration
-------------
### Dynamic method injection

The `withOraclekv()` dynamic method will be added to controllers by default. You can
change this setting by adding a configuration flag in `griffon-app/conf/Config.groovy`

    griffon.oraclekv.injectInto = ['controller', 'service']

### Events

The following events will be triggered by this addon

 * OraclekvConnectStart[config, storeName] - triggered before connecting to the store
 * OraclekvConnectEnd[storeName, store] - triggered after connecting to the store
 * OraclekvDisconnectStart[config, storeName, store] - triggered before disconnecting from the store
 * OraclekvDisconnectEnd[config, storeName] - triggered after disconnecting from the store

### Multiple Stores

The config file `OraclekvConfig.groovy` defines a default store block. As the name
implies this is the store used by default, however you can configure named stores
by adding a new config block. For example connecting to a store whose name is 'internal'
can be done in this way

	stores {
	    internal {
		    host = 'server.acme.com'
		}
	}

This block can be used inside the `environments()` block in the same way as the
default store block is used.

### Example

A trivial sample application can be found at [https://github.com/aalmiray/griffon_sample_apps/tree/master/persistence/oraclekv][2]

[1]: http://www.oracle.com/technetwork/database/nosqldb/overview/index.html
[2]: https://github.com/aalmiray/griffon_sample_apps/tree/master/persistence/oraclekv
'''
}
