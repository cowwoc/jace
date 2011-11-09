#include "jace/BaseException.h"

BEGIN_NAMESPACE_1(jace)


BaseException::BaseException(const std::string& value) throw (): 
	mValue(value)
{}

/**
 * Convert std::wstring to a modified UTF-8 std::string.
 *
 * Adaptation of u_strToJavaModifiedUTF8() from http://site.icu-project.org/
 */
std::string toUTF8(const std::wstring& src)
{
    size_t ch=0;
    size_t count;
		std::string result;

    // Faster loop without ongoing checking for pSrcLimit and pDestLimit.
		std::wstring::const_iterator i = src.begin();
		while (i!=src.end())
		{
				count = result.length();
        /*
         * Each iteration of the inner loop progresses by at most 3 UTF-8
         * bytes and one UChar.
         */
        count /= 3;
				if (count > src.length())
					count = src.length(); /* min(remaining dest/3, remaining src) */
        if(count < 3)
				{
            /*
             * Too much overhead if we get near the end of the string,
             * continue with the next loop.
             */
            break;
        }
        do
				{
            ch = *i++;
            if (ch <= 0x7f && ch != 0)
							result += (char) ch;
            else if(ch <= 0x7ff)
						{
                result += (char)((ch>>6)|0xc0);
                result += (char)((ch&0x3f)|0x80);
            }
						else
						{
                result += (char)((ch>>12)|0xe0);
                result += (char)(((ch>>6)&0x3f)|0x80);
                result += (char)((ch&0x3f)|0x80);
            }
        } while(--count > 0);
    }

		while (i != src.end())
		{
        ch = *i++;
        if (ch <= 0x7f && ch != 0)
				{
            result += (char) ch;
        }
				else if(ch <= 0x7ff)
				{
					result += (char)((ch>>6)|0xc0);
					result += (char)((ch&0x3f)|0x80);
        }
				else
				{
          result += (char)((ch>>12)|0xe0);
          result += (char)(((ch>>6)&0x3f)|0x80);
          result += (char)((ch&0x3f)|0x80);
        }
    }
    return result;
}

BaseException::BaseException(const std::wstring& value) throw ()
{
	mValue = toUTF8(value);
}

BaseException::BaseException(const BaseException& rhs) throw ()
{
  mValue = rhs.mValue;
}

BaseException& BaseException::operator=(const BaseException& rhs) throw ()
{
  if (this == &rhs)
    return *this;
  mValue = rhs.mValue;
  return *this;
}

BaseException::~BaseException() throw ()
{}

const char* BaseException::what() const throw ()
{
  return mValue.c_str();
}

END_NAMESPACE_1(jace)
