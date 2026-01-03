import Navbar from './Navbar';
import Footer from './Footer';
import styles from './layout.module.css';
import React from "react";

interface LayoutProps {
    children: React.ReactNode;
}

function Layout({ children }: LayoutProps) {
    return (
        <div className={styles.container}>
            <Navbar />
            <main className={styles.main}>{children}</main>
            <Footer />
        </div>
    );
}

export default Layout;
