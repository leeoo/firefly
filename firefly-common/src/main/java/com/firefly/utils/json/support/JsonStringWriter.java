package com.firefly.utils.json.support;

import java.util.Deque;
import java.util.LinkedList;

import com.firefly.utils.io.IOUtils;
import com.firefly.utils.io.StringWriter;
import static com.firefly.utils.json.JsonStringSymbol.QUOTE;
import static com.firefly.utils.json.JsonStringSymbol.ARRAY_PRE;
import static com.firefly.utils.json.JsonStringSymbol.ARRAY_SUF;
import static com.firefly.utils.json.JsonStringSymbol.SEPARATOR;

public class JsonStringWriter extends StringWriter {
	public final static char[] replaceChars = new char[((int) '\\' + 1)];
	static {
        replaceChars['\"'] = '"';
        replaceChars['\r'] = 'r';
        replaceChars['\n'] = 'n';
        replaceChars['\t'] = 't';
        replaceChars['\\'] = '\\';
        replaceChars['\f'] = 'f';
        replaceChars['\b'] = 'b';
	}
	private Deque<Object> deque = new LinkedList<Object>();

	public void pushRef(Object obj) {
		deque.addFirst(obj);
	}

	public boolean existRef(Object obj) {
		return deque.contains(obj);
	}

	public void popRef() {
		deque.removeFirst();
	}
	
	private void writeJsonString0(String value) {
		buf[count++] = QUOTE;
		for (char ch : value.toCharArray()) {
			if (ch == '\b' || ch == '\n' || ch == '\r' || ch == '\f'
					|| ch == '\\' || ch == '"' || ch == '\t') {
				buf[count++] = '\\';
				buf[count++] = replaceChars[(int) ch];
			} else {
				buf[count++] = ch;
			}
		}
		buf[count++] = QUOTE;
	}

	public void writeJsonString(String value) {
		int newcount = count + (value.length() * 2 + 2);
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}
		writeJsonString0(value);
	}

	public void writeStringWithQuote(final boolean quote, final String value) {
		int len = value.length();
		int newcount = count + len + (quote ? 2 : 0);
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}
		if (quote)
			buf[count++] = QUOTE;
		value.getChars(0, len, buf, count);
		count += len;
		if (quote)
			buf[count++] = QUOTE;
		
	}
	
	public void writeStringArray(String[] array) {
		int totalSize = 2;
		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
                totalSize++;
            }
			int size = array[i].length() * 2 + 2;
			
            totalSize += size;
		}
		
		int newcount = count + totalSize;
        if (newcount > buf.length) {
            expandCapacity(newcount);
        }

        buf[count++] = ARRAY_PRE;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                buf[count++] = SEPARATOR;
            }
            writeStringWithQuote(true, array[i]);
        }
        buf[count++] = ARRAY_SUF;
	}
	
	public void writeIntArray(int[] array) {
        int[] sizeArray = new int[array.length];
        int totalSize = 2;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                totalSize++;
            }
            int val = array[i];
            int size;
            if (val == Integer.MIN_VALUE) {
                size = MIN_INT_VALUE.length;
            } else {
                size = (val < 0) ? IOUtils.stringSize(-val) + 1 : IOUtils.stringSize(val);
            }
            sizeArray[i] = size;
            totalSize += size;
        }

        int newcount = count + totalSize;
        if (newcount > buf.length) {
            expandCapacity(newcount);
        }

        buf[count] = ARRAY_PRE;

        int currentSize = count + 1;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                buf[currentSize++] = SEPARATOR;
            }

            int val = array[i];
            if (val == Integer.MIN_VALUE) {
                System.arraycopy(MIN_INT_VALUE, 0, buf, currentSize, sizeArray[i]);
                currentSize += sizeArray[i];
            } else {
                currentSize += sizeArray[i];
                IOUtils.getChars(val, currentSize, buf);
            }
        }
        buf[currentSize] = ARRAY_SUF;

        count = newcount;
    }
	
	public void writeIntArray(Integer[] array) {
		int[] sizeArray = new int[array.length];
        int totalSize = 2;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                totalSize++;
            }
            int val = array[i];
            int size;
            if (val == Integer.MIN_VALUE) {
                size = MIN_INT_VALUE.length;
            } else {
                size = (val < 0) ? IOUtils.stringSize(-val) + 1 : IOUtils.stringSize(val);
            }
            sizeArray[i] = size;
            totalSize += size;
        }

        int newcount = count + totalSize;
        if (newcount > buf.length) {
            expandCapacity(newcount);
        }

        buf[count] = ARRAY_PRE;

        int currentSize = count + 1;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                buf[currentSize++] = SEPARATOR;
            }

            int val = array[i];
            if (val == Integer.MIN_VALUE) {
                System.arraycopy(MIN_INT_VALUE, 0, buf, currentSize, sizeArray[i]);
                currentSize += sizeArray[i];
            } else {
                currentSize += sizeArray[i];
                IOUtils.getChars(val, currentSize, buf);
            }
        }
        buf[currentSize] = ARRAY_SUF;

        count = newcount;
	}
	
	public void writeShortArray(short[] array) {
        int[] sizeArray = new int[array.length];
        int totalSize = 2;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                totalSize++;
            }
            short val = array[i];
            int size = IOUtils.stringSize(val);
            sizeArray[i] = size;
            totalSize += size;
        }

        int newcount = count + totalSize;
        if (newcount > buf.length) {
            expandCapacity(newcount);
        }

        buf[count] = ARRAY_PRE;

        int currentSize = count + 1;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                buf[currentSize++] = SEPARATOR;
            }

            short val = array[i];
            currentSize += sizeArray[i];
            IOUtils.getChars(val, currentSize, buf);
        }
        buf[currentSize] = ARRAY_SUF;

        count = newcount;
    }
	
	public void writeShortArray(Short[] array) {
		int[] sizeArray = new int[array.length];
        int totalSize = 2;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                totalSize++;
            }
            short val = array[i];
            int size = IOUtils.stringSize(val);
            sizeArray[i] = size;
            totalSize += size;
        }

        int newcount = count + totalSize;
        if (newcount > buf.length) {
            expandCapacity(newcount);
        }

        buf[count] = ARRAY_PRE;

        int currentSize = count + 1;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                buf[currentSize++] = SEPARATOR;
            }

            short val = array[i];
            currentSize += sizeArray[i];
            IOUtils.getChars(val, currentSize, buf);
        }
        buf[currentSize] = ARRAY_SUF;

        count = newcount;
	}
	
	public void writeLongArray(long[] array) {
        int[] sizeArray = new int[array.length];
        int totalSize = 2;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                totalSize++;
            }
            long val = array[i];
            int size;
            if (val == Long.MIN_VALUE) {
                size = MIN_LONG_VALUE.length;
            } else {
                size = (val < 0) ? IOUtils.stringSize(-val) + 1 : IOUtils.stringSize(val);
            }
            sizeArray[i] = size;
            totalSize += size;
        }

        int newcount = count + totalSize;
        if (newcount > buf.length) {
            expandCapacity(newcount);
        }

        buf[count] = ARRAY_PRE;

        int currentSize = count + 1;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                buf[currentSize++] = SEPARATOR;
            }

            long val = array[i];
            if (val == Long.MIN_VALUE) {
                System.arraycopy(MIN_LONG_VALUE, 0, buf, currentSize, sizeArray[i]);
                currentSize += sizeArray[i];
            } else {
                currentSize += sizeArray[i];
                IOUtils.getChars(val, currentSize, buf);
            }
        }
        buf[currentSize] = ARRAY_SUF;

        count = newcount;
    }
	
	public void writeLongArray(Long[] array) {
		int[] sizeArray = new int[array.length];
        int totalSize = 2;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                totalSize++;
            }
            long val = array[i];
            int size;
            if (val == Long.MIN_VALUE) {
                size = MIN_LONG_VALUE.length;
            } else {
                size = (val < 0) ? IOUtils.stringSize(-val) + 1 : IOUtils.stringSize(val);
            }
            sizeArray[i] = size;
            totalSize += size;
        }

        int newcount = count + totalSize;
        if (newcount > buf.length) {
            expandCapacity(newcount);
        }

        buf[count] = ARRAY_PRE;

        int currentSize = count + 1;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                buf[currentSize++] = SEPARATOR;
            }

            long val = array[i];
            if (val == Long.MIN_VALUE) {
                System.arraycopy(MIN_LONG_VALUE, 0, buf, currentSize, sizeArray[i]);
                currentSize += sizeArray[i];
            } else {
                currentSize += sizeArray[i];
                IOUtils.getChars(val, currentSize, buf);
            }
        }
        buf[currentSize] = ARRAY_SUF;

        count = newcount;
	}
	
	public void writeBooleanArray(boolean[] array) {
        int totalSize = 2;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                totalSize++;
            }
            boolean val = array[i];
            totalSize += val ? 4 : 5;;
        }

        int newcount = count + totalSize;
        if (newcount > buf.length) {
            expandCapacity(newcount);
        }

        buf[count] = ARRAY_PRE;

        int currentSize = count + 1;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                buf[currentSize++] = SEPARATOR;
            }

            boolean val = array[i];
            if (val) {
                buf[currentSize++] = 't';
                buf[currentSize++] = 'r';
                buf[currentSize++] = 'u';
                buf[currentSize++] = 'e';
            } else {
                buf[currentSize++] = 'f';
                buf[currentSize++] = 'a';
                buf[currentSize++] = 'l';
                buf[currentSize++] = 's';
                buf[currentSize++] = 'e';
            }
        }
        buf[currentSize] = ARRAY_SUF;

        count = newcount;
    }
	
	public void writeBooleanArray(Boolean[] array) {
		int totalSize = 2;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                totalSize++;
            }
            boolean val = array[i];
            totalSize += val ? 4 : 5;;
        }

        int newcount = count + totalSize;
        if (newcount > buf.length) {
            expandCapacity(newcount);
        }

        buf[count] = ARRAY_PRE;

        int currentSize = count + 1;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                buf[currentSize++] = SEPARATOR;
            }

            boolean val = array[i];
            if (val) {
                buf[currentSize++] = 't';
                buf[currentSize++] = 'r';
                buf[currentSize++] = 'u';
                buf[currentSize++] = 'e';
            } else {
                buf[currentSize++] = 'f';
                buf[currentSize++] = 'a';
                buf[currentSize++] = 'l';
                buf[currentSize++] = 's';
                buf[currentSize++] = 'e';
            }
        }
        buf[currentSize] = ARRAY_SUF;

        count = newcount;
	}
}
