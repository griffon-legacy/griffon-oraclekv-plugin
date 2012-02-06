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

package griffon.plugins.oraclekv;

import groovy.lang.Closure;
import griffon.util.CallableWithArgs;

/**
 * @author Andres Almiray
 */
public interface OraclekvProvider {
    Object withOraclekv(Closure closure);

    Object withOraclekv(String storeName, Closure closure);

    <T> T withOraclekv(CallableWithArgs<T> callable);

    <T> T withOraclekv(String storeName, CallableWithArgs<T> callable);
}
