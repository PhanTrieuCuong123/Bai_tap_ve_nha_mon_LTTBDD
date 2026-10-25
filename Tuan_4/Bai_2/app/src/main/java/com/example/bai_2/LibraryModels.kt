package com.example.bai_2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// ===== 1. TRỪU TƯỢNG (Abstraction) =====
// Lớp trừu tượng mô tả "một tài liệu trong thư viện"
abstract class LibraryItem(val id: Int, title: String) {

    // ===== 2. ĐÓNG GÓI (Encapsulation) =====
    // Bên ngoài chỉ đọc được title, không sửa trực tiếp
    var title: String = title
        private set

    abstract val loanDays: Int
    abstract fun getInfo(): String
}

// ===== 3. KẾ THỪA (Inheritance) =====
open class Book(id: Int, title: String) : LibraryItem(id, title) {

    var borrowedBy by mutableStateOf<Student?>(null)
        private set

    val isAvailable: Boolean get() = borrowedBy == null

    override val loanDays: Int get() = 14
    override fun getInfo(): String = "Sách: $title (mượn $loanDays ngày)"

    // Chỉ cho phép Student (cùng module) gọi
    internal fun markBorrowed(student: Student) {
        borrowedBy = student
    }

    internal fun markReturned() {
        borrowedBy = null
    }
}

// ===== 4. ĐA HÌNH (Polymorphism): ghi đè getInfo() và loanDays =====
class Textbook(id: Int, title: String, private val subject: String) : Book(id, title) {
    override val loanDays: Int get() = 30
    override fun getInfo(): String = "Giáo trình $subject: $title (mượn $loanDays ngày)"
}

class Novel(id: Int, title: String, private val author: String) : Book(id, title) {
    override val loanDays: Int get() = 7
    override fun getInfo(): String = "Tiểu thuyết: $title - $author (mượn $loanDays ngày)"
}

class Student(val id: Int, val name: String) {

    private val _borrowedBooks = mutableStateListOf<Book>()
    val borrowedBooks: List<Book> get() = _borrowedBooks

    fun borrow(book: Book): Boolean {
        if (!book.isAvailable) return false
        book.markBorrowed(this)
        _borrowedBooks.add(book)
        return true
    }

    fun giveBack(book: Book) {
        if (_borrowedBooks.remove(book)) book.markReturned()
    }
}

class LibraryManager {
    private var nextBookId = 1
    private var nextStudentId = 1

    private val _books = mutableStateListOf<Book>()
    private val _students = mutableStateListOf<Student>()

    val books: List<Book> get() = _books
    val students: List<Student> get() = _students

    var currentStudent by mutableStateOf<Student?>(null)
        private set

    init {
        addStudent("Nguyen Van A")
        addStudent("Tran Thi B")
        addBook("Sách 01")
        addBook("Sách 02")
        _books.add(Textbook(nextBookId++, "Lập trình di động", "CNTT"))
        _books.add(Novel(nextBookId++, "Dế Mèn phiêu lưu ký", "Tô Hoài"))

        currentStudent = _students.first()
        currentStudent?.borrow(_books[0])
        currentStudent?.borrow(_books[1])
    }

    fun addBook(title: String) {
        _books.add(Book(nextBookId++, title))
    }

    fun addStudent(name: String) {
        _students.add(Student(nextStudentId++, name))
    }

    fun changeStudent(student: Student) {
        currentStudent = student
    }

    fun borrow(book: Book) {
        currentStudent?.borrow(book)
    }

    fun giveBack(book: Book) {
        currentStudent?.giveBack(book)
    }
}