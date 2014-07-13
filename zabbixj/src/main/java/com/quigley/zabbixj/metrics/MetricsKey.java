/*
 * Zabbix/J - A Java agent for the Zabbix monitoring system.
 * Copyright (C) 2006-2010 Michael F. Quigley Jr.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301, USA.
 */

package com.quigley.zabbixj.metrics;

/**
 * <p>An internal class for representing the components for metric key. This 
 * class is used to convert from raw String representations of parameters to a 
 * parsed enumerated representation.</p>
 *
 * <p>Textual format keys are correspond to the strings entered within Zabbix as
 * "items". To be consistent with the Zabbix native agents, they have the
 * following format:</p>
 * 
 * <code>&lt;provider&gt;.&lt;provider_key&gt;[&lt;param1&gt;,...]</code>
 * 
 * @author Michael Quigley
 * @author Mark van Holsteijn
 */
public class MetricsKey {

	/**
	 * Construct a new MetricsKey instance.
	 * @param keyData the textual key.
	 * @throws MetricsException when there is a problem parsing the textual key.
	 */
	public MetricsKey(String keyData) throws MetricsException {
		parseKey(keyData);
	}

    public String getProvider() {
		return provider;	
	}
	
	public String getKey() {
		return key;
	}
	
	public boolean isParameters() {
        return parameters != null;	
	}
	
	public String[] getParameters() {
		return parameters;
	}

	private void parseKey(String keyData) throws MetricsException {
		try {
			// search from the last dot from the end of the string or the start of the parameter list 
            int searchBackwardFrom = (keyData.indexOf(PARAMS_START) == -1) ? keyData.length() : keyData.indexOf(PARAMS_START);
            int firstSepIdx = keyData.lastIndexOf(KEY_SEPARATOR, searchBackwardFrom);

			if(firstSepIdx > 0) {
				provider = keyData.substring(0, firstSepIdx);					
	
				String localStr = keyData.substring(firstSepIdx + 1, keyData.length());
				int startParamsIdx = localStr.indexOf(PARAMS_START) + 1;
				int endParamsIdx = localStr.lastIndexOf(PARAMS_END);
	
				if(startParamsIdx != -1 && endParamsIdx != -1) {
					key = localStr.substring(0, localStr.indexOf(PARAMS_START));
	
					String paramsStr = localStr.substring(startParamsIdx, endParamsIdx);
                    String[] paramTokens = paramsStr.split("" + PARAMS_SEPARATOR);
					parameters = new String[paramTokens.length];
                    for(int i = 0; i < paramTokens.length; i++) {
                        String paramToken = paramTokens[i];
                        if(paramToken.indexOf('"') != -1) {
                            paramToken = paramToken.substring(paramToken.indexOf('"') + 1);
                        }
                        if(paramToken.lastIndexOf('"') != -1) {
                            paramToken = paramToken.substring(0, paramToken.lastIndexOf('"'));
                        }
						parameters[i] = paramToken;
					}

				} else {
					key = localStr;	
				}
				 											
			} else {
                throw new MetricsException("Key string does not contain separator.");
			}

		} catch(Exception e) {
            throw new MetricsException("Parse error.", e);
		}
	}

    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("MetricsKey:(");
        out.append("provider:(").append(provider).append(") ");
        out.append("key:(").append(key).append(") ");
        if(parameters != null) {
            for(int i = 0; i < parameters.length; i++) {
                out.append("parameter[").append(i).append("]:(").append(parameters[i]).append(")");
            }
        }
        out.append(")");

        return out.toString();
    }

	private String provider;
	private String key;
	private String[] parameters;
	
	private static final char KEY_SEPARATOR = '.';
	private static final char PARAMS_START = '[';
	private static final char PARAMS_END = ']';
	private static final char PARAMS_SEPARATOR = ','; 
}
